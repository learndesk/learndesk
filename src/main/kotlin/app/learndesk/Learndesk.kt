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

import de.mxro.metrics.jre.Metrics
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Learndesk {
    val log = LoggerFactory.getLogger(Learndesk::class.java) as Logger
    val startTime = System.currentTimeMillis()

    val metrics = Metrics.create()!!

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

        // @todo: actual code
    }
}
