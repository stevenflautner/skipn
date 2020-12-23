package io.skipn.actions

import io.skipn.errors.BrowserOnlyFunction

actual fun routePage(route: String) {
    throw BrowserOnlyFunction
}

actual fun changeParameter(key: String, parameter: String) {
    throw BrowserOnlyFunction
}

internal actual fun updateUrlParameter(parameters: String) {
    throw BrowserOnlyFunction
}