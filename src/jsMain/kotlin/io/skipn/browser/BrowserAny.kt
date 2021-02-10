package io.skipn.browser

/**
 * Defines an element that can only be in the browser
 */
//typealias BrowserElement = Any
//
//expect fun BrowserElement.getProp(name: String)

actual open class BrowserAny(val any: Any) {

    actual fun <T> getPropOrNull(name: String): T? {
        return try {
            any.asDynamic()[name] as? T
        } catch (e: Exception) {
            null
        }
    }

    actual fun <T> getProp(name: String): T = getPropOrNull(name) ?: error("Prop was null")
}