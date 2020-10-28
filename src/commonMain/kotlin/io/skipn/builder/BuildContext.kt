@file:JvmMultifileClass
@file:JvmName("BuildContext_")

package io.skipn.builder

import io.skipn.notifiers.StatefulNotifier
import kotlinx.html.FlowContent
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

typealias StateBucket = ArrayList<StatefulNotifier<*>>

class BuildContext(
    val id: String,
    val states: HashMap<String, StateBucket> = hashMapOf(),
    val statesByClasses: HashMap<KClass<*>, Any> = hashMapOf()
) {
    var childBuilderContexts: ArrayList<BuildContext>? = null
    private var onDisposeListeners: ArrayList<() -> Unit>? = null

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

}
expect val FlowContent.buildContext: BuildContext

interface Builder {
    val rootBuildContext: BuildContext
    var currentBuildContext: BuildContext
    val builderContextTree: ArrayDeque<BuildContext>

    fun createContext(id: String): BuildContext {
        // Creates a copy of the states
        val context = BuildContext(
            id,
            HashMap(currentBuildContext.states),
            HashMap(currentBuildContext.statesByClasses)
        )
        return context
    }

    fun descendBuilder(context: BuildContext) {
        currentBuildContext.addChild(context)
        builderContextTree.addLast(context)
        currentBuildContext = context
    }

    fun ascendBuilder(id: String) {
        descendState(id)

        // Current builder ended should ascend the build context
        if (currentBuildContext.id == id) {
            // In DEV mode the root #skipn-app div ascends
            // therefore we should no longer advance to the body & html tags
            // So leave root build context as current
            if (builderContextTree.size == 1) return
            builderContextTree.removeLast()
            currentBuildContext = builderContextTree.last()
        }
    }

    private fun descendState(id: String) {
        val stateBucket = rootBuildContext.states[id] ?: return

        stateBucket.forEach { state ->
            rootBuildContext.statesByClasses.remove(state::class)
        }
        rootBuildContext.states.remove(id)
    }
}