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

package app.learndesk

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.Properties

object Mail {
    private val solicitedMails = listOf("data_harvest")
    private val locales: Map<Locale, Properties>

    init {
        val enLocales = {}.javaClass.getResource("/email/strings/en.properties").openStream()
        val en = Properties()
        en.load(enLocales)
        enLocales.close()

        locales = mapOf(
            Pair(Locale.ENGLISH, en)
        )
    }

    fun send(to: String, email: String, locale: Locale, variables: Map<String, Any> = emptyMap()) {
        val html = bakeMail(email, locale, variables)
        println(html)
    }

    private fun bakeMail(email: String, locale: Locale, variables: Map<String, Any>): String {
        val locales = locales[locale] ?: error("wtf dude")
        val html = {}.javaClass.getResource("/email/html/$email.html").readText()
        return html.replace("\\{([a-z_.]+)}".toRegex()) {
            var localeId = it.groupValues[1]
            if (localeId == "footer.reason") {
                localeId = "$localeId.${if (solicitedMails.contains(email)) '2' else '1'}"
            }
            locales[localeId].toString()
        }.replace("\\{\\{ ([a-z_]+) }}".toRegex()) {
            val replacement = variables[it.groupValues[1]] ?: ""
            if (replacement is LocalDateTime) {
                val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).localizedBy(locale)
                replacement.format(formatter)
            } else {
                replacement.toString()
            }
        }
    }
}
