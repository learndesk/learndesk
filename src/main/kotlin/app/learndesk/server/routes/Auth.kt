/*
 * Learndesk REST API
 * Copyright (C) 2019 Learndesk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.learndesk.server.routes

import app.learndesk.Learndesk
import app.learndesk.Mail
import app.learndesk.database.Account
import app.learndesk.mailcheck.Mailcheck
import app.learndesk.misc.end
import app.learndesk.misc.replyError
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.util.Locale

/**
 * Handler for auth-related routes
 *
 * @author Bowser65
 */
object Auth : AbstractRoute() {
    private val USERNAME_REGEX = "^[\\w_-]+$".toRegex()

    override fun registerRoutes(router: Router) {
        router.post("/auth/register").coroutineHandler(this::handleRegister)
        // router.post("/auth/login").handler(this::handler)
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
}
