package app.learndesk.database.entities

import io.vertx.core.json.JsonObject
import org.bson.Document

class Account(
    private val id: Long,
    private val username: String,
    private val firstname: String?,
    private val lastname: String?,
    private val birthday: String?,
    private val avatar: String?,
    private val verified: Boolean,
    private val locale: String,
    private val flags: Long,
    private val mfa: Boolean
) : IEntity {
    override fun toJson() = toJson(fname = true, lname = true, bday = true, self = true)

    fun toJson(fname: Boolean, lname: Boolean, bday: Boolean, self: Boolean): JsonObject {
        val json = JsonObject()
            .put("id", id.toString())
            .put("username", username)
            .put("firstname", if (fname) firstname else null)
            .put("lastname", if (lname) lastname else null)
            .put("birthday", if (bday) birthday else null)
            .put("avatar", avatar)
            .put("locale", locale)
            .put("flags", flags)

        if (self) {
            json.put("verified", verified).put("mfa", mfa)
        }

        return json
    }

    companion object {
        fun build(document: Document): Account {
            return Account(
                document.getLong("_id"),
                document.getString("username"),
                document.getString("firstname"),
                document.getString("lastname"),
                document.getString("birthday"),
                document.getString("avatar"),
                document.getBoolean("verified") ?: false,
                document.getString("locale") ?: "en", // @todo: make this better
                document.getLong("flags") ?: 0,
                document.getBoolean("mfa") ?: false
            )
        }
    }
}
