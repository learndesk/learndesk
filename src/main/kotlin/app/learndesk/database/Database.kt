package app.learndesk.database

import com.mongodb.client.MongoClients

object Database {
    private val client = MongoClients.create()
    private val database = client.getDatabase("learndesk")
    internal val accounts = database.getCollection("accounts")
}
