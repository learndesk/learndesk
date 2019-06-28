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

package app.learndesk.antishitcode

import app.learndesk.Mail
import org.junit.Assert.*
import org.junit.Test
import java.util.Date
import java.util.Locale
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.jvm.isAccessible

class TestMail {
    @Test
    fun testBakery() {
        val bakeMail = Mail::class.declaredMemberFunctions.find { it.name == "bakeMail" }!!
        bakeMail.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        val baked = bakeMail.call(Mail, "test", Locale.ENGLISH, mapOf(
            Pair("variable_1", "have a var"),
            Pair("variable_2", "coffee"),
            Pair("variable_3", Date(1560283713000L))
        )) as Pair<String, String>

        assertEquals("is this shitcode?", baked.first)
        assertTrue(baked.second.contains("Test 1: is this shitcode?"))
        assertTrue(baked.second.contains("Test 2: here have a random string"))
        assertTrue(baked.second.contains("Test 3: have a var"))
        assertTrue(baked.second.contains("Test 4: did ya know that coffee is p good?"))
        assertTrue(baked.second.contains("Test 5: drdisrespect did his first (and last :^)) irl stream on 6/11/19 at 10:08 PM CEST"))
    }
}
