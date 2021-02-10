package io.skipn.events

import io.skipn.browser.BrowserAny
import io.skipn.errors.BrowserOnlyFunction

//actual class Event {
//
//    actual fun preventDefault() {
//        throw BrowserOnlyFunction
//    }
//
//}
actual class BrowserEvent {

    actual val type: String
        get() = throw BrowserOnlyFunction
    actual val target: BrowserAny?
        get() = throw BrowserOnlyFunction
    actual val currentTarget: BrowserAny?
        get() = throw BrowserOnlyFunction
    actual val eventPhase: Short
        get() = throw BrowserOnlyFunction
    actual val bubbles: Boolean
        get() = throw BrowserOnlyFunction
    actual val cancelable: Boolean
        get() = throw BrowserOnlyFunction
    actual val defaultPrevented: Boolean
        get() = throw BrowserOnlyFunction
    actual val composed: Boolean
        get() = throw BrowserOnlyFunction
    actual val isTrusted: Boolean
        get() = throw BrowserOnlyFunction
    actual val timeStamp: Number
        get() = throw BrowserOnlyFunction

    actual fun stopPropagation() { throw BrowserOnlyFunction }
    actual fun stopImmediatePropagation() { throw BrowserOnlyFunction }
    actual fun preventDefault() { throw BrowserOnlyFunction }

}

