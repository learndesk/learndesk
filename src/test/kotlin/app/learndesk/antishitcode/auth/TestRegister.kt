/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk.antishitcode.auth

import app.learndesk.antishitcode.AbstractLearndeskTest
import app.learndesk.database.Account
import app.learndesk.database.Database
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.web.client.WebClient
import org.bson.Document
import org.junit.Test
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible

class TestRegister : AbstractLearndeskTest("accounts") {

    @Test
    fun testRootAccount(ctx: TestContext) {
        val createStaffAccount = Account::class.declaredMemberFunctions.find { it.name == "createStaffAccount" }!!
        createStaffAccount.isAccessible = true

        ctx.assertEquals(0L, collection.countDocuments())
        createStaffAccount.call(Account)
        ctx.assertEquals(1L, collection.countDocuments())
        createStaffAccount.call(Account)
        ctx.assertEquals(1L, collection.countDocuments())
    }

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
    fun testInvalidFields(ctx: TestContext) {
        val async = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", true)
                    .put("email", "test1@learndesk.app")
                    .put("password", "very secure password")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(0L, collection.countDocuments())
                ctx.assertEquals("invalid", it.result().bodyAsJsonObject().getJsonObject("error").getString("username"))
                async.complete()
            }
    }

    @Test
    fun testMissingFields(ctx: TestContext) {
        val async = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("email", "test1@learndesk.app")
                    .put("password", "very secure password")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(0L, collection.countDocuments())
                ctx.assertEquals("invalid", it.result().bodyAsJsonObject().getJsonObject("error").getString("username"))
                async.complete()
            }
    }

    @Test
    fun testWrongUsernameFormat(ctx: TestContext) {
        val async = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", "haha haha")
                    .put("email", "test1@learndesk.app")
                    .put("password", "very secure password")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(0L, collection.countDocuments())
                ctx.assertEquals("format", it.result().bodyAsJsonObject().getJsonObject("error").getString("username"))
                async.complete()
            }
    }

    @Test
    fun testWrongUsernameLength(ctx: TestContext) {
        val async = ctx.async()
        val async2 = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", "a")
                    .put("email", "test1@learndesk.app")
                    .put("password", "very secure password")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(0L, collection.countDocuments())
                ctx.assertEquals("length", it.result().bodyAsJsonObject().getJsonObject("error").getString("username"))
                async.complete()
            }

        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    .put("email", "test1@learndesk.app")
                    .put("password", "very secure password")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(0L, collection.countDocuments())
                ctx.assertEquals("length", it.result().bodyAsJsonObject().getJsonObject("error").getString("username"))
                async2.complete()
            }
    }

    @Test
    fun testWrongEmail(ctx: TestContext) {
        val async = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", "test1")
                    .put("email", "no u")
                    .put("password", "very secure password")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(0L, collection.countDocuments())
                ctx.assertEquals("format", it.result().bodyAsJsonObject().getJsonObject("error").getString("email"))
                async.complete()
            }
    }

    @Test
    fun testWrongPassword(ctx: TestContext) {
        val async = ctx.async()
        val async2 = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", "test1")
                    .put("email", "test1@learndesk.app")
                    .put("password", "a")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(0L, collection.countDocuments())
                ctx.assertEquals("length", it.result().bodyAsJsonObject().getJsonObject("error").getString("password"))
                async.complete()
            }

        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", "test1")
                    .put("email", "test1@learndesk.app")
                    .put("password", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(0L, collection.countDocuments())
                ctx.assertEquals("length", it.result().bodyAsJsonObject().getJsonObject("error").getString("password"))
                async2.complete()
            }
    }

    @Test
    fun testTakenUsername(ctx: TestContext) {
        Database.accounts.insertOne(Document("username", "test1"))

        val async = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", "test1")
                    .put("email", "test1@learndesk.app")
                    .put("password", "very secure password")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(1L, collection.countDocuments())
                ctx.assertEquals("taken", it.result().bodyAsJsonObject().getJsonObject("error").getString("username"))
                async.complete()
            }
    }

    @Test
    fun testTakenEmail(ctx: TestContext) {
        Database.accounts.insertOne(Document("email_sanitized", "test1@learndesk.app"))

        val async = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", "test1")
                    .put("email", "test1@learndesk.app")
                    .put("password", "very secure password")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(1L, collection.countDocuments())
                ctx.assertEquals("taken", it.result().bodyAsJsonObject().getJsonObject("error").getString("email"))
                async.complete()
            }
    }

    @Test
    fun testTakenEmailAlias(ctx: TestContext) {
        Database.accounts.insertOne(Document("email_sanitized", "test1@learndesk.app"))

        val async = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.post(8000, "localhost", "/auth/register")
            .sendJsonObject(
                JsonObject()
                    .put("username", "test1")
                    .put("email", "test1+gotem@learndesk.app")
                    .put("password", "very secure password")
            ) {
                ctx.assertEquals(400, it.result().statusCode())
                ctx.assertEquals(1L, collection.countDocuments())
                ctx.assertEquals("taken", it.result().bodyAsJsonObject().getJsonObject("error").getString("email"))
                async.complete()
            }
    }
}
