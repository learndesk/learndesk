/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk.server.routes

import app.learndesk.Learndesk
import app.learndesk.database.entities.AccountEntity
import app.learndesk.database.Account as DBAccount
import app.learndesk.misc.replyError
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import xyz.bowser65.tokenize.IAccount
import java.util.concurrent.CompletableFuture
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
        teacher: Boolean = false,
        // Overload memes because java big stupid
        @Suppress("UNUSED_PARAMETER") __overload_gay: Boolean = false
    ) {
        coroutineHandler Suspendable@{ ctx ->
            val token = ctx.request().getHeader("authorization")
                ?: return@Suspendable ctx.replyError(401, "missing token")

            @Suppress("UNCHECKED_CAST") // Java have the gayest gay
            val fetcher = { id: String -> DBAccount.fetchFuture(id) } as (String) -> CompletableFuture<IAccount>

            val account =
                Learndesk.tokenize.validate(token, fetcher, ctx.request().path().startsWith("/auth/mfa")).await()
                    as? AccountEntity ?: return@Suspendable ctx.replyError(401, "invalid token")

            if (
                (staff && !account.isStaff()) ||
                (teacher && !account.isTeacher() && !account.isStaff())
            ) return@Suspendable ctx.replyError(403, "insufficient permissions")

            fn(ctx, account)
        }
    }

    fun Route.authenticatedHandler(
        fn: (RoutingContext, AccountEntity) -> Unit,
        staff: Boolean = false,
        teacher: Boolean = false
    ) {
        authenticatedHandler(
            Suspendable@{ ctx: RoutingContext, acc: AccountEntity -> fn(ctx, acc) },
            staff, teacher, false
        )
    }

    abstract fun registerRoutes(router: Router)
}
