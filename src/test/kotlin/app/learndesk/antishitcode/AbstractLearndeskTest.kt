/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/ocl>.
 */

package app.learndesk.antishitcode

import app.learndesk.Learndesk
import app.learndesk.database.Database
import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.RunTestOnContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.bson.Document
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@RunWith(value = VertxUnitRunner::class)
abstract class AbstractLearndeskTest(private val collectionName: String? = null) {
    @get:Rule
    var rule = RunTestOnContext()

    protected lateinit var collection: MongoCollection<Document>

    @Before
    fun cleanDatabase() {
        if (collectionName == null) return
        if (!::collection.isInitialized) {
            val field = Database::class.memberProperties.find { it.name == collectionName }!!
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            collection = field.getter.call(Database) as MongoCollection<Document>
        }
        collection.deleteMany(BasicDBObject())
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupLearndesk(ctx: TestContext) {
            val async = ctx.async()
            try {
                Learndesk.startup().thenAccept { async.complete() }
            } catch (_: Throwable) {
                // this is fine, gradle starts the server automatically not intellij
                async.complete()
            }
        }
    }
}
