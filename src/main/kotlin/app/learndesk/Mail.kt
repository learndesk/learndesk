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

import org.apache.commons.mail.HtmlEmail
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.Properties
import java.util.concurrent.Executors

object Mail {
    private val log = LoggerFactory.getLogger(Mail::class.java) as Logger
    private val solicitedMails = listOf("data_harvest")
    private val locales = mutableMapOf<Locale, Properties>()

    private val scheduler = Executors.newSingleThreadExecutor()!!

    init {
        // LOCALES
        mapOf(
            Pair(Locale.ENGLISH, "en")
        ).forEach { (locale, id) ->
            val strings = {}.javaClass.getResource("/email/strings/$id.properties").openStream()
            val props = Properties()
            props.load(strings)
            strings.close()
            locales[locale] = props
        }
    }

    fun send(to: String, emailId: String, locale: Locale, variables: Map<String, Any> = emptyMap()) {
        val mail = bakeMail(emailId, locale, variables)
        try {
            val email = HtmlEmail()
            email.hostName = Learndesk.properties.getProperty("smtp.host")
            email.subject = mail.first

            email.addTo(to)
            email.setCharset("UTF-32")
            email.setHtmlMsg(mail.second)
            email.setSmtpPort(Learndesk.properties.getProperty("smtp.port").toInt())
            email.setFrom("noreply@learndesk.app")
            scheduler.submit {
                email.send()
            }
        } catch (e: Throwable) {
            log.error("Failed to send email!", e)
        }
    }

    private fun bakeMail(email: String, locale: Locale, variables: Map<String, Any>): Pair<String, String> {
        val locales = locales[locale] ?: locales[Locale.ENGLISH]!!
        var subject = ""
        var html = {}.javaClass.getResource("/email/html/$email.html").readText()
        html = html.replace("\\{([a-z_.]+)}".toRegex()) {
            var localeId = it.groupValues[1]
            if (localeId == "footer.reason") {
                localeId = "$localeId.${if (solicitedMails.contains(email)) '2' else '1'}"
            }
            val localized = locales.getProperty(localeId)
            if (localeId.startsWith("title.")) {
                subject = localized
            }
            localized
        }.replace("\\{\\{ ([a-z_]+) }}".toRegex()) {
            val replacement = variables[it.groupValues[1]] ?: ""
            if (replacement is LocalDateTime) {
                val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).localizedBy(locale)
                replacement.format(formatter)
            } else {
                replacement.toString()
            }
        }
        return Pair(subject, html)
    }
}
