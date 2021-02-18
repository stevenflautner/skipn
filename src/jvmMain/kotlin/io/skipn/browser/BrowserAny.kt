package io.skipn.browser

import io.skipn.errors.BrowserOnlyFunction

/**
 * Defines an element that can only be in the browser
 */
//typealias BrowserElement = Any
//
//expect fun BrowserElement.getProp(name: String)

actual open class BrowserAny {
    actual fun <T> getPropOrNull(name: String): T? {
        throw BrowserOnlyFunction
    }
    actual fun <T> getProp(name: String): T {
        throw BrowserOnlyFunction
    }
}