package io.skipn.browser

/**
 * Defines an element that can only be in the browser
 */
expect open class BrowserAny {
    fun <T> getPropOrNull(name: String): T?
    fun <T> getProp(name: String): T
}