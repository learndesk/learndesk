package app.learndesk.server

import app.learndesk.Learndesk
import app.learndesk.Version
import app.learndesk.server.routes.Account
import app.learndesk.server.routes.Coffee
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
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
            it.response()
                .setStatusCode(500)
                .setStatusMessage("Internal Server Error")
                .putHeader("Content-Type", "application/json")
                .end(encodeError(500, "an internal server error occurred"))
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
            it.response().putHeader("Learndesk-Commit", Version.COMMIT)
            it.response().putHeader("Content-Type", "application/json")

            // @todo: metrics (probably datadog)
            it.next()
        }

        // Routes
        Coffee.registerRoutes(router)
        Account.registerRoutes(router)

        // 404
        router.route().handler {
            it.response()
                .setStatusCode(404)
                .setStatusMessage("Not Found")
                .end(encodeError(404, "no endpoint matching your query found"))
        }

        // Listen
        httpServer.requestHandler(router).listen(Learndesk.PORT) {
            if (it.failed()) {
                log.error("Failed to start HTTP server!", it.cause())
            } else {
                started = true
                log.info("HTTP server started successfully, ready to handle requests")
            }
        }
    }

    fun encodeError(status: Int, error: String): Buffer {
        return JsonObject()
            .put("status", status)
            .put("error", error)
            .toBuffer()
    }
}
