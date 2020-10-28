package io.skipn.actions

import io.skipn.Skipn
import io.skipn.platform.DEV
import kotlinx.browser.window

actual fun routePage(route: String) {
    if (!DEV) {
        window.history.pushState(null, route, route)
    }
    Skipn.context.route.setValue(route)
}