/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk.database

import app.learndesk.Learndesk
import com.mongodb.client.MongoClients

object Database {
    private val client = MongoClients.create()
    private val database = client.getDatabase(Learndesk.properties.getProperty("database"))

    internal val accounts = database.getCollection("accounts")
}
