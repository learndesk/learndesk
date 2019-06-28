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

package app.learndesk.server

import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext

/**
 * Handler for form bodies sent to the Server
 *
 * @author Bowser65
 */
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
