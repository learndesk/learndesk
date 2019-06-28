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

package app.learndesk.misc

import app.learndesk.Learndesk
import java.util.concurrent.atomic.AtomicLong

/**
 * Snowflake generator used by Learndesk
 *
 * @author Bowser65
 */
class Snowflake(
    private val workerId: Long,
    private val resourceId: Long
) {
    private val workerIdBits = 5L
    private val resourceIdBits = 5L
    private val sequenceBits = 12L

    private val maxSequence = -1L xor (-1L shl sequenceBits.toInt())

    private val timestampShift = sequenceBits + workerIdBits + resourceIdBits
    private val workerIdShift = sequenceBits + resourceIdBits
    private val resourceIdShift = sequenceBits

    private var sequence = 0L
    private var lastTimestamp = -1L
    private val waitCount = AtomicLong(0)

    /**
     * Generates a new Snowflake If worker and resource IDs are properly set the ID will be guaranteed to be
     * unique. Else there is a low chance that you'll get a duplicate
     *
     * @return The generated snowflake.
     */
    @Synchronized
    fun nextId(): Long {
        var currTimestamp = timestampGen()

        if (currTimestamp < lastTimestamp) {
            throw IllegalStateException(
                String.format(
                    "Clock moved backwards. Refusing to generate id for %d milliseconds",
                    lastTimestamp - currTimestamp
                )
            )
        }

        if (currTimestamp == lastTimestamp) {
            sequence = sequence + 1 and maxSequence
            if (sequence == 0L) {
                currTimestamp = waitNextMillis(currTimestamp)
            }
        } else {
            sequence = 0L
        }

        lastTimestamp = currTimestamp

        return currTimestamp - Learndesk.LEARNDESK_EPOCH shl timestampShift.toInt() or
            (workerId shl workerIdShift.toInt()) or
            (resourceId shl resourceIdShift.toInt()) or
            sequence
    }

    private fun waitNextMillis(oCurrTimestamp: Long): Long {
        var currTimestamp = oCurrTimestamp
        waitCount.incrementAndGet()
        while (currTimestamp <= lastTimestamp) {
            currTimestamp = timestampGen()
        }
        return currTimestamp
    }

    private fun timestampGen(): Long {
        return System.currentTimeMillis()
    }
}
