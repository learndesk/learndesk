/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk.antishitcode

import io.vertx.ext.unit.TestContext
import io.vertx.ext.web.client.WebClient
import org.junit.Test

class TestCoffee : AbstractLearndeskTest() {
    @Test
    fun teapot(ctx: TestContext) {
        val async = ctx.async()
        val client = WebClient.create(rule.vertx())
        client.get(8000, "localhost", "/coffee")
            .send {
                ctx.assertEquals(418, it.result().statusCode())
                ctx.assertEquals("i'm a teapot", it.result().bodyAsJsonObject().getString("error"))
                async.complete()
            }
    }
}
