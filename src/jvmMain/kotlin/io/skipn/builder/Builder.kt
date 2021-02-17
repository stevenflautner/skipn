package io.skipn.builder

import io.skipn.html.HTMLStreamBuilder
import kotlinx.html.FlowContent
import kotlinx.html.Tag
import kotlinx.html.consumers.DelayedConsumer
import kotlinx.html.consumers.FinalizeConsumer

actual val Tag.builder: Builder
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

actual interface Builder {

    actual val rootBuildContext: BuildContext
    actual var currentBuildContext: BuildContext

    val routerTree: ArrayDeque<Tag>

    fun descendRoute(tag: Tag) {
        rootBuildContext.routeLevel++
        routerTree.addLast(tag)
    }

    fun ascendRoute(tag: Tag) {
        if (routerTree.lastOrNull() !== tag) return
        rootBuildContext.routeLevel--
        routerTree.removeLast()
    }

}