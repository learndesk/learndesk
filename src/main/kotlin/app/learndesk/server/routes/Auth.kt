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
import app.learndesk.database.Account
import app.learndesk.mailcheck.Mailcheck
import app.learndesk.misc.end
import app.learndesk.misc.replyError
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

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
        val errors = validateAuth(body)
        if (!errors.isEmpty) {
            return ctx.replyError(400, errors)
        }

        val email = body.getString("email")
        val username = body.getString("username")
        val password = body.getString("password")
        val account = Account.createAccount(Mailcheck.buildEmail(email), username, password)
        ctx.response().end(account)
    }

    // +------------+
    // | Validators |
    // +------------+
    private suspend fun validateAuth(data: JsonObject): JsonObject {
        val email = data.getValue("email")
        val username = data.getValue("username")
        val password = data.getValue("password")
        val response = JsonObject()

        if (email == null || email !is String) response.put("email", "invalid")
        if (username == null || username !is String) response.put("username", "invalid")
        if (password == null || password !is String) response.put("password", "invalid")

        if (response.isEmpty) {
            // Cast shit
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
