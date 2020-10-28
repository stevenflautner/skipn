package io.skipn.builder

import kotlinx.html.FlowContent
import kotlinx.html.consumers.DelayedConsumer
import kotlinx.html.consumers.FinalizeConsumer
import kotlinx.html.dom.JSDOMBuilder
import kotlinx.html.stream.HTMLStreamBuilder

actual val FlowContent.buildContext: BuildContext
    get() = builder.currentBuildContext

val FlowContent.builder: Builder
    get() {
        var consumer = consumer

        // Dynamically built tree
        if (consumer is JSDOMBuilder)
            return consumer
        if (consumer is FinalizeConsumer<*, *>) {
            consumer = consumer.downstream
            if (consumer is JSDOMBuilder)
                return consumer
        }

        // Server preloaded tree
        if (consumer is DelayedConsumer) {
            consumer = consumer.downstream
            if (consumer is FinalizeConsumer<*, *>) {
                consumer = consumer.downstream
                if (consumer is HTMLStreamBuilder)
                    return consumer
            }
        }

        throw Exception("JSDomBuilder could not be found while building")
    }