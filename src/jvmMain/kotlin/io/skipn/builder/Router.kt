package io.skipn.builder

import io.skipn.errors.BrowserOnlyFunction

actual class Router actual constructor(fullRoute: String) : RouterBase(fullRoute) {

    actual fun changeParameter(key: String, newValue: Any?) {
        throw BrowserOnlyFunction
    }

    actual fun changeRoute(fullRoute: String) {
        throw BrowserOnlyFunction
    }

    actual fun updateRoute(newRouteValues: List<String>) {
        throw BrowserOnlyFunction
    }

    actual fun updateParameters(newParameters: Parameters) {
        throw BrowserOnlyFunction
    }
}