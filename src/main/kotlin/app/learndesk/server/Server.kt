/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
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
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.sstore.LocalSessionStore
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import java.util.concurrent.CompletableFuture

object Server {
    private val log: Logger = LoggerFactory.getLogger(Server::class.java)
    private var started = false

    private val vertx = Vertx.vertx()
    private val router = Router.router(vertx)
    private val httpServer = vertx.createHttpServer()

    fun startup(): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        if (started) {
            throw IllegalStateException("Server is already up!")
        }

        // Error handler
        router.route().failureHandler {
            log.error("Error in HTTP handler", it.failure())
            it.replyError(500, "an internal server error occurred")
        }

        // Session
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)))

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

        router.route().handler(MemoryBodyHandler())

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
        httpServer.requestHandler(router).listen(Learndesk.properties.getProperty("port").toInt()) {
            if (it.failed()) {
                log.error("Failed to start HTTP server!", it.cause())
                future.completeExceptionally(it.cause())
            } else {
                started = true
                log.info("HTTP server started successfully, ready to handle requests")
                future.complete(null)
            }
        }
        return future
    }
}
