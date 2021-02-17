package io.skipn

import io.ktor.application.*
import io.skipn.platform.SkipnResources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.html.FlowContent

//actual val FlowContent.skipnContext: SkipnContext
//get() {
//    var consumer = consumer
//    if (consumer is DelayedConsumer) {
//        consumer = consumer.downstream
//        if (consumer is FinalizeConsumer<*, *>)
//            consumer = consumer.downstream
//    }
//    return (consumer as HTMLStreamBuilder).skipnContext
//}

fun FlowContent.prepareElement() {
//    val context = skipnContext

//    val id = attributes["id"] ?: context.points.generateId().also {
//        attributes["id"] = it
//    }

//    return id
}

actual class SkipnContext(route: String, val applicationCall: ApplicationCall) : SkipnContextBase(route) {

    actual var isInitializing = true
    actual val points = Elements()
    actual val resources = SkipnResources()

}