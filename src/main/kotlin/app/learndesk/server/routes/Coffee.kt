/*
 * Learndesk REST API
 * Copyright (C) 2019, Learndesk. All Rights Reserved.
 *
 * This program is licensed under the Open Core License.
 * You should have received a copy of the license along with
 * this program. If not, see <https://oss.learndesk.app/open-core-license>.
 */

package app.learndesk.server.routes

import app.learndesk.misc.replyError
import io.vertx.ext.web.Router

object Coffee : AbstractRoute() {
    override fun registerRoutes(router: Router) {
        router.get("/coffee").handler {
            it.replyError(418, "i'm a teapot")
        }
    }
}
