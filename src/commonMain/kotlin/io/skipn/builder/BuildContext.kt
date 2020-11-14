@file:JvmMultifileClass
@file:JvmName("BuildContext_")

package io.skipn.builder

import io.skipn.provide.PinningContext
import kotlinx.coroutines.*
import kotlinx.html.FlowContent
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

class BuildContext(
    val id: String,
    val pinningContext: PinningContext
) {
    var childBuilderContexts: ArrayList<BuildContext>? = null
    private var onDisposeListeners: ArrayList<() -> Unit>? = null

    lateinit var coroutineScope: CoroutineScope

    fun addChild(context: BuildContext) {
        val childBuilderContexts = childBuilderContexts ?: arrayListOf<BuildContext>().also {
            this.childBuilderContexts = it
        }
        childBuilderContexts.add(context)
    }
    fun disposeChildrenTree() {
        notifyDisposeListeners()
        childBuilderContexts?.forEach {
            it.disposeChildrenTree()
        }
        childBuilderContexts = null
    }

    private fun notifyDisposeListeners() {
        onDisposeListeners?.forEach {
            it()
        }
        onDisposeListeners = null
    }

    fun onDispose(onDispose: () -> Unit) {
        val onDisposeListeners = onDisposeListeners ?: arrayListOf<() -> Unit>().also {
            this.onDisposeListeners = it
        }
        onDisposeListeners.add(onDispose)
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) {
        coroutineScope.launch(block = block)
    }
}
expect val FlowContent.buildContext: BuildContext

interface Builder {
    val rootBuildContext: BuildContext
    var currentBuildContext: BuildContext
    val builderContextTree: ArrayDeque<BuildContext>

    fun createContextAndDescend(id: String): BuildContext {
        // Creates a copy of the states
        val context = BuildContext(
            id,
            PinningContext(parent = currentBuildContext.pinningContext)
        )
        context.coroutineScope = CoroutineScope(SupervisorJob(currentBuildContext.coroutineScope.coroutineContext.job))

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

fun FlowContent.launch(block: suspend CoroutineScope.() -> Unit) {
    buildContext.launch(block = block)
}
