package io.skipn.actions

import io.skipn.Skipn
import io.skipn.platform.DEV
import kotlinx.browser.window

actual fun routePage(route: String) {
    if (!DEV) {
        window.history.pushState(null, route, route)
    }
    println("PAGE ROUTED ${route}")

    Skipn.context.router.changeRoute(route)
//    val curRoute = Skipn.context.route.value
//    Skipn.context.route.value = Route(route, curRoute.route)
}

class RouteState(val url: String)

actual fun changeParameter(key: String, parameter: String) {
    Skipn.context.router.changeParameter(key, parameter)
}

internal actual fun updateUrlParameter(parameters: String) {
    val route = if (parameters.isEmpty()) window.location.pathname
    else window.location.pathname + "?${parameters.removePrefix("&")}"
    window.history.pushState(null, route, route)
}