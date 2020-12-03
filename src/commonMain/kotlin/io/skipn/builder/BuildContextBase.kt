package io.skipn.builder

import io.skipn.SkipnContext
import io.skipn.provide.PinningContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BuildContextBase(
        val id: String,
        val skipnContext: SkipnContext,
        val pinningContext: PinningContext
) {
    var childBuilderContexts: ArrayList<BuildContextBase>? = null
    private var onDisposeListeners: ArrayList<() -> Unit>? = null

    fun addChild(context: BuildContextBase) {
        val childBuilderContexts = childBuilderContexts ?: arrayListOf<BuildContextBase>().also {
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