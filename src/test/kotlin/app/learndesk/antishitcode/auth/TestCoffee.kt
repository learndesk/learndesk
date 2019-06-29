/*
 * Learndesk REST API
 * Copyright (C) 2019 Learndesk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.learndesk.antishitcode.auth

import app.learndesk.antishitcode.AbstractLearndeskTest
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
