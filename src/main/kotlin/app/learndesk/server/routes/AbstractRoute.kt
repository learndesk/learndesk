/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/ocl>.
 */

package app.learndesk.server.routes

import app.learndesk.database.entities.AccountEntity
import app.learndesk.misc.Token
import app.learndesk.misc.replyError
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

abstract class AbstractRoute : CoroutineScope {
    override val coroutineContext: CoroutineContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

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

    fun Route.authenticatedHandler(
        fn: suspend (RoutingContext, AccountEntity) -> Unit,
        staff: Boolean = false,
        teacher: Boolean = false
    ) {
        coroutineHandler Suspendable@{ ctx ->
            val token = ctx.request().getHeader("authorization")
                ?: return@Suspendable ctx.replyError(401, "missing token")
            val account = Token.validate(token, ctx.request().path().startsWith("/auth/mfa"))
                ?: return@Suspendable ctx.replyError(401, "invalid token")

            if (
                (staff && !account.isStaff()) ||
                (teacher && !account.isTeacher() && !account.isStaff())
            ) return@Suspendable ctx.replyError(403, "insufficient permissions")

            fn(ctx, account)
        }
    }

    abstract fun registerRoutes(router: Router)
}
