package io.skipn

import io.skipn.platform.DEV
import io.skipn.platform.SkipnResources
import io.skipn.utils.byId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.html.FlowContent
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

actual val FlowContent.skipnContext: SkipnContext
get() {
    return Skipn.context
//    val consumer = consumer
//    if (consumer is DelayedConsumer)
//        return (consumer.downstream as HTMLStreamBuilder).elementHandler
//    return (consumer as JSDOMBuilder).elementHandler
}

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

actual class SkipnContext(route: String) {

    actual var isInitializing = true
    actual var route = MutableStateFlow(Route(route))
    actual val points = Elements()
    actual val resources = SkipnResources()

}