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

package app.learndesk.server

import app.learndesk.Learndesk
import app.learndesk.Version
import app.learndesk.misc.replyError
import app.learndesk.server.routes.Account
import app.learndesk.server.routes.Auth
import app.learndesk.server.routes.Coffee
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException

object Server {
    private val log = LoggerFactory.getLogger(Server::class.java) as Logger
    private var started = false

    private val vertx = Vertx.vertx()
    private val router = Router.router(vertx)
    private val httpServer = vertx.createHttpServer()

    fun startup() {
        if (started) {
            throw IllegalStateException("Server is already up!")
        }

        // Error handler
        router.route().failureHandler {
            log.error("Error in HTTP handler", it.failure())
            it.replyError(500, "an internal server error occurred")
        }

        // Request headers
        router.route().handler {
            // Debug logs
            log.debug(
                "Received request {} {} from {}",
                it.request().method(),
                it.normalisedPath(),
                it.request().remoteAddress()
            )

            // Headers
            it.response().putHeader("learndesk-commit", Version.COMMIT)
            it.response().putHeader("content-type", "application/json")

            // @todo: metrics (probably datadog)
            it.next()
        }

        // Routes
        Coffee.registerRoutes(router)
        Auth.registerRoutes(router)
        Account.registerRoutes(router)

        // Handle OPTIONS to ensure CORS requests don't fail
        router.options().handler { it.response().end() }

        // 404
        router.route().handler {
            it.replyError(404, "no endpoint matching your query found")
        }

        // Listen
        httpServer.requestHandler(router).listen(Learndesk.properties.getProperty("port", "8000").toInt()) {
            if (it.failed()) {
                log.error("Failed to start HTTP server!", it.cause())
            } else {
                started = true
                log.info("HTTP server started successfully, ready to handle requests")
            }
        }
    }
}
