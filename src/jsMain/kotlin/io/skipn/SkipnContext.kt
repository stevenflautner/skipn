package io.skipn

import io.skipn.builder.buildContext
import io.skipn.platform.DEV
import io.skipn.platform.SkipnResources
import io.skipn.utils.byId
import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.html.FlowContent
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import kotlin.js.Date

fun FlowContent.getUnderlyingHtmlElement(): HTMLElement {
    var d = this.consumer.asDynamic()
    if (d.downstream != null) {
        d = d.downstream
    }
    val arr = d.path_0.toArray() as Array<HTMLElement>
    return arr[arr.size - 1]
}

fun FlowContent.prepareElement(): Element {
    val context = skipnContext

    val id = attributes["id"] ?: context.points.generateId().also {
        attributes["id"] = it
    }

    return if (context.isInitializing && !DEV)
        byId(id)
    else getUnderlyingHtmlElement()
}

actual class SkipnContext(route: String) : SkipnContextBase(route) {

    actual var isInitializing = true
    actual val points = Elements()
    actual val resources = SkipnResources()

    inline fun initialize(body: () -> Unit) {
        val ct = Date().getMilliseconds()
        body()
        isInitializing = false
        if (DEV) {
            println("Skipn initialized, took ${Date().getMilliseconds() - ct} ms")
        }
    }

}

inline fun createSkipnContext(body: (SkipnContext) -> Unit): SkipnContext {
    val ct = Date().getMilliseconds()

    return SkipnContext(window.location.href).apply {
        body(this)
        isInitializing = false
        if (DEV) {
            println("Skipn initialized, took ${Date().getMilliseconds() - ct} ms")
        }
    }
}