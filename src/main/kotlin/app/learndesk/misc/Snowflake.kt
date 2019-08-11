/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk.misc

import app.learndesk.Learndesk
import java.util.concurrent.atomic.AtomicLong

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
