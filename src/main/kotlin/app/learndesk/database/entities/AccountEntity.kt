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

package app.learndesk.database.entities

import io.vertx.core.json.JsonObject
import org.bson.Document

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
    val flags: Int,
    val mfa: Boolean,
    // Internal fields
    val tokenTime: Long,
    val resetRequired: Boolean
) : IEntity {
    fun isStaff() = flags and (1 shl 0) != 0
    fun isTeacher() = flags and (1 shl 1) != 0
    fun isContributor() = flags and (1 shl 2) != 0
    fun isBugHunter() = flags and (1 shl 3) != 0

    // General shit
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
                document.getInteger("flags") ?: 0,
                document.getBoolean("mfa") ?: false,

                document.getLong("token_time"),
                document.getBoolean("reset_required") ?: false
            )
        }
    }
}
