package app.learndesk.database.entities

import io.vertx.core.json.JsonObject

interface IEntity {
    fun toJson(): JsonObject
}
