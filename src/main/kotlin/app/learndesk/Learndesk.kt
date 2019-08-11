/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk

import app.learndesk.server.Server
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.bowser65.tokenize.Tokenize
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

object Learndesk {
    const val LEARNDESK_EPOCH = 1546300800000L
    val log: Logger = LoggerFactory.getLogger(Learndesk::class.java)
    val startTime = System.currentTimeMillis()
    lateinit var tokenize: Tokenize
        private set

    // Config
    val properties: Properties = Properties()

    @JvmStatic
    fun main(args: Array<String>) {
        startup()
    }

    fun startup(): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        log.info("~~~ Learndesk REST API ~~~")
        if (Version.COMMIT.contains("@")) {
            log.info("Running in debug mode")
        } else {
            log.info("Running in Production mode")
            log.info("Version ${Version.VERSION}")
            log.info("Git revision ${Version.COMMIT}")
        }

        log.info("Loading config properties")
        loadProperties()
        tokenize = Tokenize(properties.getProperty("auth.secret"))

        log.info("Starting vert.x HTTP server")
        Server.startup().thenAccept {
            log.info("Learndesk startup complete!")
            future.complete(null)
        }

        return future
    }

    private fun loadProperties() {
        val cfg = this.javaClass.classLoader.getResourceAsStream("cfg.properties")
        if (cfg != null) {
            properties.load(cfg)
            cfg.close()
        }
        val config = File("./config.properties")
        if (config.canRead()) {
            val inputStream = config.inputStream()
            properties.load(inputStream)
            inputStream.close()
        }
    }
}
