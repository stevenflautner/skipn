package io.skipn.browser

expect open class BrowserElement {
    fun setClasses(classes: String)
    fun setWidth(width: Double)
    fun setHeight(height: Double)
    fun getWidth(): Double
    fun getHeight(): Double
    fun getAttribute(name: String): String?
    fun getLeft(): Double
    fun getTop(): Double
    fun setLeft(left: Double)
    fun setTop(top: Double)
    fun getScrollTop(): Double
    fun setScrollTop(scrollTop: Double)
    fun getScrollLeft(): Double
    fun setScrollLeft(scrollLeft: Double)
    fun focus()
}