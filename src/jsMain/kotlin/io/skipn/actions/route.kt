package io.skipn.actions

import io.skipn.Route
import io.skipn.Skipn
import io.skipn.platform.DEV
import kotlinx.browser.window

actual fun routePage(route: String) {
    if (!DEV) {
        window.history.pushState(null, route, route)
    }
    val curRoute = Skipn.context.route.value
    Skipn.context.route.value = Route(route, curRoute.route)
}