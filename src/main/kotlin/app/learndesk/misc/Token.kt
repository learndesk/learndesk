/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */
package app.learndesk.misc

/*
import app.learndesk.Learndesk
import app.learndesk.database.Account
import app.learndesk.database.entities.AccountEntity
import java.lang.StringBuilder
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Token {
    private const val VERSION = 1

    fun generate(id: String, mfa: Boolean): String {
        val builder = StringBuilder()
        if (mfa) builder.append("mfa.")
        builder.append(String(Base64.getEncoder().encode(id.toByteArray(Charsets.UTF_8))))
        builder.append(".")
        builder.append(String(Base64.getEncoder().encode(computeTokenTime().toString().toByteArray(Charsets.UTF_8))))
        val part = builder.toString()
        return "$part.${sign(part)}".replace("=", "")
    }

    suspend fun validate(token: String, ignoreMfa: Boolean = false): AccountEntity? {
        // Deconstruct token
        val mfaToken = token.contains("mfa.")
        val tok = token.replace("mfa.", "").split('.')
        val accountId = tok[0]
        val tokenTime = tok[1]
        val signature = tok[2]

        // Validate signature
        val expectedSign = sign("${if (mfaToken) "mfa." else ""}$accountId.$tokenTime").replace("=", "")
        if (expectedSign != signature) {
            return null
        }

        // Query database
        val account = Account.fetch(accountId) ?: return null
        if (
            account.tokenTime > tokenTime.toLong() ||
            !ignoreMfa && account.mfa != mfaToken ||
            ignoreMfa && mfaToken ||
            account.resetRequired
        ) return null
        return account
    }

    fun computeTokenTime(): Long {
        return ((System.currentTimeMillis() - Learndesk.LEARNDESK_EPOCH) / 1000.0).toLong()
    }

    private fun sign(data: String): String {
        val hmac = Mac.getInstance("HmacSHA256")
        val key = SecretKeySpec(
            Learndesk.properties.getProperty("auth.secret").toByteArray(Charsets.UTF_8), "HmacSHA256"
        )
        hmac.init(key)

        return String(Base64.getEncoder().encode(hmac.doFinal("$data.$VERSION".toByteArray(Charsets.UTF_8))))
    }
}
*/
