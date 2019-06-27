package app.learndesk.misc

import app.learndesk.database.entities.IEntity
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

fun RoutingContext.replyError(code: Int, message: Any) {
    this.response()
        .setStatusCode(code)
        .end(
            JsonObject()
                .put("status", code)
                .put("error", message)
                .toBuffer()
        )
}

fun HttpServerResponse.end(entity: IEntity) {
    this.end(entity.toJson().toBuffer())
}
