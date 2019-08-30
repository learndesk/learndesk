/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk.database.entities

import io.vertx.core.json.JsonObject
import org.bson.Document
import xyz.bowser65.tokenize.IAccount

@Suppress("MemberVisibilityCanBePrivate")
class AccountEntity(
    val id: Long,
    val username: String,
    val firstname: String?,
    val lastname: String?,
    val birthday: String?,
    val avatar: String?,
    val verified: Boolean,
    val locale: String,
    val theme: String,
    val flags: Int,
    val mfa: Boolean,
    // Internal fields
    val tokenTime: Long,
    val resetRequired: Boolean,
    val banned: Boolean
) : IEntity, IAccount {
    fun isStaff() = flags and (1 shl 0) != 0
    fun isTeacher() = flags and (1 shl 1) != 0
    fun isContributor() = flags and (1 shl 2) != 0
    fun isBugHunter() = flags and (1 shl 3) != 0
    fun isTranslator() = flags and (1 shl 4) != 0

    // Tokenize
    override fun hasMfa() = mfa
    override fun tokensValidSince() = tokenTime

    // General shit
    override fun toJson() = toJson(true)

    fun toJson(fname: Boolean = true, lname: Boolean = true, bday: Boolean = true, self: Boolean = true): JsonObject {
        val json = JsonObject()
            .put("id", id.toString())
            .put("username", username)
            .put("firstname", if (fname) firstname else null)
            .put("lastname", if (lname) lastname else null)
            .put("birthday", if (bday) birthday else null)
            .put("avatar", avatar)
            .put("theme", theme)
            .put("locale", locale)
            .put("flags", flags)

        if (self) {
            json.put("verified", verified).put("mfa", mfa)
        }

        return json
    }

    companion object {
        fun build(document: Document): AccountEntity {
            return AccountEntity(
                document.getLong("_id"),
                document.getString("username"),
                document.getString("firstname"),
                document.getString("lastname"),
                document.getString("birthday"),
                document.getString("avatar"),
                document.getBoolean("verified") ?: false,
                document.getString("locale") ?: "en", // @todo: make this better
                document.getString("theme") ?: "dark",
                document.getInteger("flags") ?: 0,
                document.getBoolean("mfa") ?: false,

                document.getLong("token_time"),
                document.getBoolean("reset_required") ?: false,
                document.getBoolean("banned") ?: false
            )
        }
    }
}
