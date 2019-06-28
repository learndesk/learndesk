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

package app.learndesk.misc

import app.learndesk.database.entities.IEntity
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

/**
 * Replies with an error. Automatically sets status code, body and sends it to the client
 *
 * @param code Status code
 * @param message Message sent in the payload. Can be anything that is accepted by JsonObject::put
 */
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

/**
 * Replies with a Learndesk entity. The entity will be serialized to JSON
 *
 * @param entity The entity
 */
fun HttpServerResponse.end(entity: IEntity) {
    this.end(entity.toJson().toBuffer())
}
