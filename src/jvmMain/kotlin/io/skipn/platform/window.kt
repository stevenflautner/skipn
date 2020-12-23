package io.skipn.platform

import io.skipn.errors.BrowserOnlyFunction

actual class Window {
    actual val width: Int get() = throw BrowserOnlyFunction
    actual val height: Int get() = throw BrowserOnlyFunction
}