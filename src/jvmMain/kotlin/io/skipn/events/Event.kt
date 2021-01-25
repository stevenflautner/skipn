package io.skipn.events

import io.skipn.errors.BrowserOnlyFunction

actual class Event {

    actual fun preventDefault() {
        throw BrowserOnlyFunction
    }

}