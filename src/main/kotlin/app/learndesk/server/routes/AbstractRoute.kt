package app.learndesk.server.routes

import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServerRequest
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
                } catch (e: Exception) {
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
