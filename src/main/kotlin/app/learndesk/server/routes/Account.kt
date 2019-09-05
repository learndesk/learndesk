/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk.server.routes

import app.learndesk.database.entities.AccountEntity
import app.learndesk.misc.end
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

object Account : AbstractRoute() {
    override fun registerRoutes(router: Router) {
        router.get("/account/me").authenticatedHandler(this::handleMe)
    }

    private fun handleMe(ctx: RoutingContext, account: AccountEntity) {
        ctx.response().end(account)
    }
}
