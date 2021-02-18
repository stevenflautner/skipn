package io.skipn.builder

import io.skipn.html.JSDOMBuilder
import io.skipn.html.SkipnInitializationBuilder
import kotlinx.html.FlowContent
import kotlinx.html.FlowOrMetaDataOrPhrasingContent
import kotlinx.html.TagConsumer
import kotlinx.html.consumers.DelayedConsumer
import kotlinx.html.consumers.FinalizeConsumer

actual val FlowContent.builder: Builder
    get() = getBuilder(consumer)

val FlowOrMetaDataOrPhrasingContent.builder: Builder
    get() = getBuilder(consumer)

private fun getBuilder(_consumer: TagConsumer<*>): Builder {
    var consumer = _consumer

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
        if (consumer is SkipnInitializationBuilder) {
            return consumer
        }
    }

    throw Exception("Builder could not be found while building in the browser")
}

actual interface Builder {
    actual val rootBuildContext: BuildContext

    actual var currentBuildContext: BuildContext
    val builderContextTree: ArrayDeque<BuildContext>

    fun createContextAndDescend(id: String, routeLevel: Int = currentBuildContext.getRouteLevel()): BuildContext {
        // Creates a copy of the states
        val context = BuildContext.create(id, currentBuildContext, routeLevel)

        descendBuilder(context)
        return context
    }

    fun descendBuilder(context: BuildContext) {
//        currentBuildContext.addChild(context)
        builderContextTree.addLast(context)
        currentBuildContext = context
    }

    // Ascends the Builder with the
    // id of the element that ended
    fun ascend(id: String) {
        ascendContext(id)
    }

    private fun ascendContext(elemId: String) {
        currentBuildContext.pinningContext.ascend(elemId)

        // Current builder ended should ascend the build context
        // Leave the root in the context tree
        if (currentBuildContext.id == elemId && builderContextTree.size > 1) {
            // In DEV mode the root #skipn-app div ascends
            // therefore we should no longer advance to the body & html tags
            // So leave root build context as current
//            if (builderContextTree.size == 1) return
            builderContextTree.removeLast()
            currentBuildContext = builderContextTree.last()
        }
    }
}