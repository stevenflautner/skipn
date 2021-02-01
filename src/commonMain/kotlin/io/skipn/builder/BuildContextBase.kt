package io.skipn.builder

import io.skipn.SkipnContext
import io.skipn.provide.PinningContext

abstract class BuildContextBase(
        val id: String,
        val skipnContext: SkipnContext,
        val pinningContext: PinningContext,
) {
    var childBuilderContexts: ArrayList<BuildContextBase>? = null
    private var onDisposeListeners: ArrayList<() -> Unit>? = null
    private lateinit var parent: BuildContextBase

    private fun addChild(context: BuildContextBase) {
        val childBuilderContexts = childBuilderContexts ?: arrayListOf<BuildContextBase>().also {
            this.childBuilderContexts = it
        }
        childBuilderContexts.add(context)
    }

    private fun detach() {
        parent.childBuilderContexts?.remove(this)
    }

    fun attach(parent: BuildContextBase) {
        this.parent = parent
        parent.addChild(this)
    }

    fun disposeChildrenTree() {
        detach()
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