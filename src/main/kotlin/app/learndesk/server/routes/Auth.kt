/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/ocl>.
 */

package app.learndesk.server.routes

import app.learndesk.Learndesk
import app.learndesk.Mail
import app.learndesk.database.Account
import app.learndesk.mailcheck.Mailcheck
import app.learndesk.misc.Token
import app.learndesk.misc.end
import app.learndesk.misc.replyError
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.util.Locale

object Auth : AbstractRoute() {
    private val USERNAME_REGEX = "^[\\w_-]+$".toRegex()

    override fun registerRoutes(router: Router) {
        router.post("/auth/register").coroutineHandler(this::handleRegister)
        router.post("/auth/login").coroutineHandler(this::handleLogin)
        // router.post("/auth/mfa").handler(this::handler)
        // router.post("/auth/reset").handler(this::handler)
        // router.post("/auth/reset/execute").handler(this::handler)
    }

    private suspend fun handleRegister(ctx: RoutingContext) {
        if (Learndesk.properties.getProperty("registering") == "false") {
            return ctx.replyError(403, "registrations disabled")
        }

        val body = ctx.bodyAsJson ?: return ctx.replyError(400, "bad or missing payload")
        val errors = validateRegister(body)
        if (!errors.isEmpty) {
            return ctx.replyError(400, errors)
        }

        val email = body.getString("email")
        val username = body.getString("username")
        val password = body.getString("password")
        val account = Account.create(Mailcheck.buildEmail(email), username, password)
        ctx.response().end(account)

        // @todo
        Mail.send(
            email, "email_confirm", Locale.ENGLISH, mapOf(
                Pair("pseudo", username),
                Pair("link", "@todo")
            )
        )
    }

    private suspend fun handleLogin(ctx: RoutingContext) {
        val body = ctx.bodyAsJson ?: return ctx.replyError(400, "bad or missing payload")
        val errors = validateLogin(body)
        if (!errors.isEmpty) {
            return ctx.replyError(400, errors)
        }

        val username = body.getString("username")
        val password = body.getString("password")
        val account = Account.fetchAuth(username, password) ?: return ctx.replyError(401, "invalid credentials")
        val response = JsonObject()
            .put("token", Token.generate(account.id.toString(), false))
            .put("mfa_required", account.mfa)

        if (!account.mfa) {
            response.put("reset_required", account.resetRequired)
        }

        ctx.response().end(response.toBuffer())
    }

    // +------------+
    // | Validators |
    // +------------+
    private suspend fun validateRegister(data: JsonObject): JsonObject {
        val email = data.getValue("email")
        val username = data.getValue("username")
        val password = data.getValue("password")
        val response = JsonObject()

        if (email == null || email !is String) response.put("email", "invalid")
        if (username == null || username !is String) response.put("username", "invalid")
        if (password == null || password !is String) response.put("password", "invalid")

        if (response.isEmpty) {
            // Cast shit
            // Those might not be considered as "safe", but we reject them if they are
            // null or not a string before. While it's hard for automated tools to detect it,
            // it's 100% safe to cast them.
            email as String
            username as String
            password as String

            if (!Mailcheck.isValidEmail(email)) response.put("email", "format")
            else if (Account.isEmailTaken(Mailcheck.buildEmail(email))) response.put("email", "taken")

            if (!USERNAME_REGEX.matches(username)) response.put("username", "format")
            else if (username.length > 32 || username.length < 2) response.put("username", "length")
            else if (Account.isUsernameTaken(username)) response.put("username", "taken")

            if (password.length > 128 || password.length < 6) response.put("password", "length")
        }
        return response
    }

    private fun validateLogin(data: JsonObject): JsonObject {
        val username = data.getValue("username")
        val password = data.getValue("password")
        val response = JsonObject()

        if (username == null || username !is String) response.put("username", "invalid")
        if (password == null || password !is String) response.put("password", "invalid")

        return response
    }
}
