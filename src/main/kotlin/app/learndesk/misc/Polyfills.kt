package app.learndesk.misc

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

fun RoutingContext.replyError(code: Int, message: String) {
    this.response()
        .setStatusCode(code)
        .end(
            JsonObject()
                .put("status", code)
                .put("error", message)
                .toBuffer()
        )
}
