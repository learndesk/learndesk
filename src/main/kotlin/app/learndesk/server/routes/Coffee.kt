package app.learndesk.server.routes

import app.learndesk.server.Server
import io.vertx.ext.web.Router

object Coffee {
    fun registerRoutes(router: Router) {
        router.get("/coffee").handler {
            it.response()
                .setStatusCode(418)
                .setStatusMessage("I'm a teapot")
                .end(Server.encodeError(418, "i'm a teapot"))
        }
    }
}
