/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk.server

import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext

class MemoryBodyHandler : Handler<RoutingContext> {
    override fun handle(context: RoutingContext) {
        val request = context.request()
        if (request.headers().contains(HttpHeaders.UPGRADE, HttpHeaders.WEBSOCKET, true)) {
            context.next()
            return
        }
        if (context.get<Any>(BODY_HANDLED) != null) {
            context.next()
        } else {
            val h = ReadHandler(context, 10 * 1024 * 1024)
            request.handler(h)
            request.endHandler { h.onEnd() }
            context.put(BODY_HANDLED, true)
        }
    }

    private class ReadHandler(private val context: RoutingContext, private val maxSize: Int) : Handler<Buffer> {
        private val buffer = Buffer.buffer()

        override fun handle(data: Buffer) {
            if (maxSize != -1 && buffer.length() + data.length() > maxSize) {
                return context.fail(413)
            }
            buffer.appendBuffer(data)
        }

        internal fun onEnd() {
            context.body = buffer
            context.next()
        }
    }

    companion object {
        private const val BODY_HANDLED = "__body-handled"
    }
}
