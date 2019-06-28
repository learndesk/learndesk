package app.learndesk.database

import app.learndesk.Learndesk
import com.mongodb.client.MongoClients

object Database {
    private val client = MongoClients.create()
    private val database = client.getDatabase(Learndesk.properties.getProperty("database"))
    internal val accounts = database.getCollection("accounts")
}
