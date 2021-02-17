package io.skipn.builder

import VNode
import kotlinx.html.FlowContent
import kotlinx.html.FlowOrMetaDataOrPhrasingContent
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.consumers.DelayedConsumer
import kotlinx.html.consumers.FinalizeConsumer
import snabbdom.SnabbdomBuilder

actual val Tag.builder: Builder
    get() = getBuilder(consumer)

val FlowOrMetaDataOrPhrasingContent.builder: Builder
    get() = getBuilder(consumer)

//TODO ONLY RETURN SNABBDOMBUILDER
private fun getBuilder(_consumer: TagConsumer<*>): Builder {
    var consumer = _consumer

    // Dynamically built tree
    if (consumer is SnabbdomBuilder)
        return consumer
    if (consumer is FinalizeConsumer<*, *>) {
        consumer = consumer.downstream
        if (consumer is SnabbdomBuilder)
            return consumer
    }

    // Server preloaded tree
    if (consumer is DelayedConsumer) {
        consumer = consumer.downstream
        if (consumer is SnabbdomBuilder) {
            return consumer
        }
    }

    throw Exception("Builder could not be found while building in the browser")
}

actual interface Builder {
    actual val rootBuildContext: BuildContext

    actual var currentBuildContext: BuildContext
    val builderContextTree: ArrayDeque<BuildContext>

    fun createContextAndDescend(vNode: VNode, routeLevel: Int = currentBuildContext.getRouteLevel()): BuildContext {
        // Creates a copy of the states
        val context = BuildContext.create(vNode, currentBuildContext, routeLevel)

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
    fun ascend(vNode: VNode, tag: Tag) {
        ascendContext(vNode, tag)
    }

    //TODO SHOULD CALL ON THE SERVER
    private fun ascendContext(vNode: VNode, tag: Tag) {
        currentBuildContext.pinningContext.ascend(tag)

        // Current builder ended should ascend the build context
        // Leave the root in the context tree
        if (currentBuildContext.vNode === vNode && builderContextTree.size > 1) {
            // In DEV mode the root #skipn-app div ascends
            // therefore we should no longer advance to the body & html tags
            // So leave root build context as current
//            if (builderContextTree.size == 1) return
            builderContextTree.removeLast()
            currentBuildContext = builderContextTree.last()
        }
    }
}