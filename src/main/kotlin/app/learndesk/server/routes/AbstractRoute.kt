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

package app.learndesk.server.routes

import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

/**
 * Represents a route
 *
 * @author Bowser65
 */
abstract class AbstractRoute : CoroutineScope {
    override val coroutineContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    /**
     * Allows using a suspend handler
     *
     * @param fn The handler
     */
    fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit) {
        handler { ctx ->
            launch(ctx.vertx().dispatcher()) {
                try {
                    fn(ctx)
                } catch (e: Throwable) {
                    ctx.fail(e)
                }
            }
        }
    }

    // This is code I took for another project (that I own mmlol)
    // It'll need to be adapted to our needs. @see REFERENCE.md
    //
    // fun Route.authenticatedHandler(fn: suspend (RoutingContext, User) -> Unit) {
    //     handler { ctx ->
    //         launch(ctx.vertx().dispatcher()) {
    //             try {
    //                 val token = ctx.request().getHeader("authorization")
    //                        ?: return@launch ctx.replyError(401, "missing token")
    //                 val resp = BricoloAPI.requestUtil.get("https://discordapp.com/api/users/@me", createHeaders(Pair("Authorization", "Bearer $token"))).await()?.json()
    //                         ?: return@launch ctx.replyError(401, "invalid token")
    //
    //                 fn(ctx, User.build(resp, token))
    //             } catch (e: Exception) {
    //                 ctx.fail(e)
    //             }
    //         }
    //     }
    // }

    abstract fun registerRoutes(router: Router)
}
