/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/ocl>.
 */

package app.learndesk.database

import app.learndesk.Learndesk
import app.learndesk.database.entities.AccountEntity
import app.learndesk.mailcheck.Email
import app.learndesk.misc.Snowflake
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import de.mkammerer.argon2.Argon2Factory
import kotlinx.coroutines.future.await
import org.bson.Document
import xyz.bowser65.tokenize.Tokenize
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

object Account {
    private const val ARGON_ITERATION = 3
    private const val ARGON_MEMORY = 128000
    private const val ARGON_PARALLELISM = 4

    private val snowflake = Snowflake(Learndesk.properties.getProperty("worker").toLong(), 0)
    private val executor = Executors.newSingleThreadExecutor()
    private val argon2d = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2d)

    init {
        createStaffAccount()
    }

    // +-------------------+
    // | Data manipulation |
    // +-------------------+
    suspend fun create(email: Email, username: String, password: String): AccountEntity {
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
                    .append("token_time", Tokenize.currentTokenTime())
            )
            future.complete(Database.accounts.find(BasicDBObject("_id", id)).first())
        }
        val document = future.await()
        return AccountEntity.build(document)
    }

    fun fetchFuture(id: String): CompletableFuture<AccountEntity> {
        val future = CompletableFuture<AccountEntity>()
        executor.submit {
            val document = Database.accounts.find(BasicDBObject("_id", id)).first()
            if (document == null) {
                future.complete(null)
            } else {
                future.complete(AccountEntity.build(document))
            }
        }
        return future
    }

    suspend fun fetch(id: String): AccountEntity? {
        return fetchFuture(id).await()
    }

    suspend fun fetchEmail(email: String): AccountEntity? {
        val future = CompletableFuture<AccountEntity?>()
        executor.submit {
            val document = Database.accounts.find(BasicDBObject("email", email)).first()
            if (document == null) {
                future.complete(null)
            } else {
                future.complete(AccountEntity.build(document))
            }
        }
        return future.await()
    }

    suspend fun fetchAuth(username: String, password: String): AccountEntity? {
        val future = CompletableFuture<AccountEntity?>()
        executor.submit {
            val or = BasicDBList()
            or.add(BasicDBObject("email", username))
            or.add(BasicDBObject("username", username))
            val document = Database.accounts.find(BasicDBObject("\$or", or)).first()
            if (document == null || !argon2d.verify(document.getString("password"), password)) {
                future.complete(null)
            } else {
                future.complete(AccountEntity.build(document))
            }
        }
        return future.await()
    }

    // +---------------+
    // | Data checking |
    // +---------------+
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

    // +---------+
    // | Helpers |
    // +---------+
    private fun createStaffAccount() {
        if (Database.accounts.countDocuments() == 0L) {
            val id = snowflake.nextId()
            val hashedPassword = argon2d.hash(ARGON_ITERATION, ARGON_MEMORY, ARGON_PARALLELISM, "i am root")
            Database.accounts.insertOne(
                Document("_id", id)
                    .append("username", "root")
                    .append("email", "root@learndesk.app")
                    .append("email_sanitized", "root@learndesk.app")
                    .append("password", hashedPassword)
                    .append("token_time", Tokenize.currentTokenTime())
                    .append("reset_required", true)
            )
        }
    }
}
