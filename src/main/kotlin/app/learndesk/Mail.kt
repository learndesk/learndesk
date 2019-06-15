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
