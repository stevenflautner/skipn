package io.skipn.builder

import kotlinx.html.FlowContent

interface Builder {

    val rootBuildContext: BuildContext
    var currentBuildContext: BuildContext
    val builderContextTree: ArrayDeque<BuildContext>

    fun createContextAndDescend(id: String): BuildContext {
//        val context = BuildContext(
//            currentBuildContext.skipnContext,
//            id,
//            PinningContext(parent = currentBuildContext.pinningContext)
//        )
//        context.coroutineScope = CoroutineScope(SupervisorJob(currentBuildContext.coroutineScope.coroutineContext.job))

        // Creates a copy of the states
        val context = BuildContext.create(id, currentBuildContext)

        descendBuilder(context)
        return context
    }

    fun descendBuilder(context: BuildContext) {
        currentBuildContext.addChild(context)
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

expect val FlowContent.builder: Builder