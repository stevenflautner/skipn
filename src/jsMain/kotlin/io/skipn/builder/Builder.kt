package io.skipn.builder

import io.skipn.html.HTMLStreamBuilder
import io.skipn.html.JSDOMBuilder
import kotlinx.html.FlowContent
import kotlinx.html.consumers.DelayedConsumer
import kotlinx.html.consumers.FinalizeConsumer

actual val FlowContent.builder: Builder
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