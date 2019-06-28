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
abstract class AbstractLearndeskTest(private val collectionName: String) {
    @get:Rule
    var rule = RunTestOnContext()

    protected lateinit var collection: MongoCollection<Document>

    @Before
    fun cleanDatabase() {
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
            Learndesk.startup().thenAccept { async.complete() }
        }
    }
}
