package io.skipn.browser

import io.skipn.errors.BrowserOnlyFunction

actual class BrowserFormElement : BrowserElement() {
    actual fun submit() {
        throw BrowserOnlyFunction
    }
}