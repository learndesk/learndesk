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
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties
import java.util.TimeZone
import java.util.concurrent.Executors

/**
 * Mail sending utility
 *
 * @author Bowser65
 */
object Mail {
    private val log = LoggerFactory.getLogger(Mail::class.java) as Logger
    private val solicitedMails = listOf("data_harvest")
    private val locales = mutableMapOf<Locale, Pair<Properties, DateFormat>>()

    private val scheduler = Executors.newSingleThreadExecutor()

    init {
        // LOCALES
        mapOf(
            Pair(Locale.ENGLISH, "en")
        ).forEach { (locale, id) ->
            val strings = {}.javaClass.getResource("/email/strings/$id.properties").openStream()
            val props = Properties()
            props.load(strings)
            strings.close()
            val formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, locale)
            formatter.timeZone = TimeZone.getTimeZone("Europe/Paris")
            locales[locale] = Pair(props, formatter)
        }
    }

    /**
     * Formats and sends an email to someone
     *
     * @param to Email address of the recipient
     * @param emailId Filename of the email, see /email/mjml
     * @param locale Locale the mail should be formatted in
     * @param variables Variables that'll get injected into the mail
     */
    fun send(to: String, emailId: String, locale: Locale, variables: Map<String, Any> = emptyMap()) {
        if (Learndesk.properties.getProperty("smtp.host") == null) {
            log.warn("Attempted to send an email, but SMTP is not configured! Email won't be sent.")
            return
        }

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
            scheduler.submit { email.send() }
        } catch (e: Throwable) {
            log.error("Failed to send email!", e)
        }
    }

    /**
     * Formats an email
     *
     * @param email Filename of the email, see /email/mjml
     * @param locale Locale the mail should be formatted in
     * @param variables Variables that'll get injected into the mail
     */
    private fun bakeMail(email: String, locale: Locale, variables: Map<String, Any>): Pair<String, String> {
        val locales = locales[locale] ?: locales[Locale.ENGLISH] ?: error("you live in another dimension.")
        var subject = ""
        var html = {}.javaClass.getResource("/email/html/$email.html").readText()
        html = html.replace("\\{([a-z0-9_.]+)}".toRegex()) {
            var localeId = it.groupValues[1]
            if (localeId == "footer.reason") {
                localeId = "$localeId.${if (solicitedMails.contains(email)) '2' else '1'}"
            }
            val localized = locales.first.getProperty(localeId)
            if (localeId.startsWith("title.")) {
                subject = localized
            }
            localized
        }.replace("\\{\\{ ([a-z0-9_]+) }}".toRegex()) {
            when (val replacement = variables[it.groupValues[1]] ?: "") {
                is Date -> {
                    locales.second.format(replacement).replace(", (\\d{1,2}:\\d{1,2}):\\d{1,2}".toRegex(), " at $1")
                }
                else -> replacement.toString()
            }
        }
        return Pair(subject, html)
    }
}
