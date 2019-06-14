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

import app.learndesk.server.Server
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.IllegalStateException
import java.util.*

object Learndesk {
    val log = LoggerFactory.getLogger(Learndesk::class.java) as Logger
    val startTime = System.currentTimeMillis()

    // Config
    var PORT = 0
        private set
    var AUTH_SECRET = ""
        private set

    @JvmStatic
    fun main(args: Array<String>) {
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

        log.info("Starting vert.x HTTP server")
        Server.startup()

        log.info("Learndesk startup complete!")
    }

    private fun loadProperties() {
        val inputStream = File("./config.properties").inputStream()
        val properties = Properties()
        properties.load(inputStream)
        inputStream.close()
        PORT = properties.getProperty("port", "8000").toInt()
        AUTH_SECRET = properties.getProperty("auth.secret")
            ?: throw IllegalStateException("auth.secret property can't be null!")
    }
}
