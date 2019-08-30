/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk.misc

import app.learndesk.database.entities.IEntity
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonArray
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

fun HttpServerResponse.end(obj: JsonObject) {
    this.end(obj.toBuffer())
}

fun HttpServerResponse.end(obj: JsonArray) {
    this.end(obj.toBuffer())
}

