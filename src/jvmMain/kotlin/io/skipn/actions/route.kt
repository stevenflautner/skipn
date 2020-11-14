package io.skipn.actions

import io.skipn.errors.BrowserOnlyFunction

actual fun routePage(route: String) {
    throw BrowserOnlyFunction
}