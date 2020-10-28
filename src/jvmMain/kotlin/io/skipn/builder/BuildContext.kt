package io.skipn.builder

import kotlinx.html.FlowContent
import kotlinx.html.consumers.DelayedConsumer
import kotlinx.html.consumers.FinalizeConsumer
import kotlinx.html.stream.HTMLStreamBuilder

actual val FlowContent.buildContext: BuildContext
    get() = builder.rootBuildContext

val FlowContent.builder: HTMLStreamBuilder<*>
    get() {
        var consumer = consumer
        if (consumer is DelayedConsumer) {
            consumer = consumer.downstream
            if (consumer is FinalizeConsumer<*, *>)
                consumer = consumer.downstream
        }
        return consumer as HTMLStreamBuilder
//        throw Exception("JSDomBuilder could not be found while building")
    }