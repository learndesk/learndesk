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

/**
 * Represents an user account
 *
 * @author Bowser65
 */
class Account(
    private val id: Long,
    private val username: String,
    private val firstname: String?,
    private val lastname: String?,
    private val birthday: String?,
    private val avatar: String?,
    private val verified: Boolean,
    private val locale: String,
    private val flags: Int,
    private val mfa: Boolean
) : IEntity {
    override fun toJson() = toJson(fname = true, lname = true, bday = true, self = true)

    /**
     * Serializes the entity to JSON with optional fields
     *
     * @param fname If the first name is publicly available
     * @param lname If the last name is publicly available
     * @param bday If the birthday date is publicly available
     * @param self If the private data should be included in the final json
     * @return The serialized JSON
     */
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
        /**
         * Builds an account from a MongoDB document. Document can be partial
         *
         * @param document The document
         * @return The built account
         */
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
                document.getInteger("flags") ?: 0,
                document.getBoolean("mfa") ?: false
            )
        }
    }
}
