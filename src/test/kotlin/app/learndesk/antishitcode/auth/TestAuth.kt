package app.learndesk.antishitcode.auth

import app.learndesk.antishitcode.AbstractLearndeskTest
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.web.client.WebClient
import org.junit.Test

class TestAuth : AbstractLearndeskTest("accounts") {
    @Test
    fun testRegister(ctx: TestContext) {
        val async = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", "test1")
                    .put("email", "test1@learndesk.app")
                    .put("password", "very secure password")
            ) {
                ctx.assertEquals(200, it.result().statusCode())
                ctx.assertEquals(1L, collection.countDocuments())
                async.complete()
            }
    }

    @Test
    fun testFirstUserStaff() {
    }

    @Test
    fun testWrongUsername() {
    }

    @Test
    fun testWrongEmail() {
    }

    @Test
    fun testWrongPassword() {
    }

    @Test
    fun testTakenUsername() {
    }

    @Test
    fun testTakenEmail() {
    }
}
