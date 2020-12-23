package io.skipn.platform

import kotlinx.browser.window

actual class Window {
    actual val width: Int = window.innerWidth
    actual val height: Int = window.innerHeight
}