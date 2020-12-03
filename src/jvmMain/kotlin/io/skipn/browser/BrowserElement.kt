package io.skipn.browser

import io.skipn.errors.BrowserOnlyFunction

actual open class BrowserElement {
    actual fun setClasses(classes: String) {
        throw BrowserOnlyFunction
    }
    actual fun setWidth(width: Double) {
        throw BrowserOnlyFunction
    }
    actual fun setHeight(height: Double) {
        throw BrowserOnlyFunction
    }
    actual fun getWidth(): Double {
        throw BrowserOnlyFunction
    }
    actual fun getHeight(): Double {
        throw BrowserOnlyFunction
    }
    actual fun getAttribute(name: String): String? {
        throw BrowserOnlyFunction
    }
    actual fun getLeft(): Double {
        throw BrowserOnlyFunction
    }
    actual fun getTop(): Double {
        throw BrowserOnlyFunction
    }
    actual fun setLeft(left: Double) {
        throw BrowserOnlyFunction
    }
    actual fun setTop(top: Double) {
        throw BrowserOnlyFunction
    }
    actual fun getScrollTop(): Double {
        throw BrowserOnlyFunction
    }
    actual fun setScrollTop(scrollTop: Double) {
      throw BrowserOnlyFunction
    }
    actual fun getScrollLeft(): Double {
      throw BrowserOnlyFunction
    }
    actual fun setScrollLeft(scrollLeft: Double) {
      throw BrowserOnlyFunction
    }
    actual fun focus() {
        throw BrowserOnlyFunction
    }
}