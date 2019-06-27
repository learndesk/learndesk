package app.learndesk.database

import app.learndesk.Learndesk
import app.learndesk.database.entities.Account as AccountEntity
import app.learndesk.mailcheck.Email
import app.learndesk.misc.Snowflake
import com.mongodb.BasicDBObject
import de.mkammerer.argon2.Argon2Factory
import kotlinx.coroutines.future.await
import org.bson.Document
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

object Account {
    private var haveDocuments = Database.accounts.find().first() != null

    private const val ARGON_ITERATION = 3
    private const val ARGON_MEMORY = 128000
    private const val ARGON_PARALLELISM = 4

    private val snowflake = Snowflake(Learndesk.properties.getProperty("worker").toLong(), 0)
    private val executor = Executors.newSingleThreadExecutor()
    private val argon2d = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2d)

    suspend fun createAccount(email: Email, username: String, password: String): AccountEntity {
        val future = CompletableFuture<Document>()
        executor.submit {
            val id = snowflake.nextId()
            val hashedPassword = argon2d.hash(ARGON_ITERATION, ARGON_MEMORY, ARGON_PARALLELISM, password)
            Database.accounts.insertOne(
                Document("_id", id)
                    .append("username", username)
                    .append("email", email.toString())
                    .append("email_sanitized", email.sanitized)
                    .append("password", hashedPassword)
                    .append("flags", if (isFirstDocument()) 1 else 0)
            )
            future.complete(Database.accounts.find(BasicDBObject("_id", id)).first())
        }
        val document = future.await()
        return AccountEntity.build(document)
    }

    suspend fun isUsernameTaken(username: String): Boolean {
        val future = CompletableFuture<Boolean>()
        executor.submit {
            future.complete(Database.accounts.find(BasicDBObject("username", username)).first() != null)
        }
        return future.await()
    }

    suspend fun isEmailTaken(email: Email): Boolean {
        val future = CompletableFuture<Boolean>()
        executor.submit {
            future.complete(Database.accounts.find(BasicDBObject("email_sanitized", email.sanitized)).first() != null)
        }
        return future.await()
    }

    private fun isFirstDocument(): Boolean {
        val res = !haveDocuments
        haveDocuments = true
        return res
    }
}
