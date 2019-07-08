/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/ocl>.
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
