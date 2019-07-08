/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/ocl>.
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

object Mail {
    private val log: Logger = LoggerFactory.getLogger(Mail::class.java)
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
            localized ?: localeId
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
