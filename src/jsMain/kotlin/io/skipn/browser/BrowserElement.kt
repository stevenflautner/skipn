package io.skipn.browser

import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

actual open class BrowserElement(val domElem: Element) {
    actual fun setClasses(classes: String) {
        domElem.className = classes
    }

    actual fun getWidth() = domElem.getBoundingClientRect().width
    actual fun getHeight() = domElem.getBoundingClientRect().height

    actual fun setWidth(width: Double) {
        if (domElem is HTMLElement) {
            domElem.style.width = "${width}px"
        }
    }

    actual fun setHeight(height: Double) {
        if (domElem is HTMLElement) {
            domElem.style.height = "${height}px"
        }
    }

    actual fun getAttribute(name: String): String? {
        if (name == "value" && domElem is HTMLInputElement) {
            return domElem.value
        }
        return domElem.getAttribute(name)
    }

    actual fun getTop() = domElem.getBoundingClientRect().top
    actual fun getLeft() = domElem.getBoundingClientRect().left

    actual fun setLeft(left: Double) {
        if (domElem is HTMLElement) {
            domElem.style.left = "${left}px"
        }
    }
    actual fun setTop(top: Double) {
        if (domElem is HTMLElement) {
            domElem.style.top = "${top.toInt()}px"
        }
    }
//    actual fun getY() = domElem.getBoundingClientRect().height
}