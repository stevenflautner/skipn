package io.skipn

import kotlinx.html.FlowContent
import kotlinx.html.consumers.DelayedConsumer
import kotlinx.html.consumers.FinalizeConsumer
import kotlinx.html.stream.HTMLStreamBuilder

actual val FlowContent.skipnContext: SkipnContext
get() {
    var consumer = consumer
    if (consumer is DelayedConsumer) {
        consumer = consumer.downstream
        if (consumer is FinalizeConsumer<*, *>)
            consumer = consumer.downstream
    }
    return (consumer as HTMLStreamBuilder).skipnContext
}

fun FlowContent.prepareElement(): String {
    val context = skipnContext

    val id = attributes["id"] ?: context.points.generateId().also {
        attributes["id"] = it
    }

    return id
}

