package io.skipn.observers

class Scope {
    var childBuilderContexts: ArrayList<Scope>? = null
    private var onDisposeListeners: ArrayList<() -> Unit>? = null
    private lateinit var parent: Scope

    private fun addChild(scope: Scope) {
        val children = childBuilderContexts ?: arrayListOf<Scope>().also {
            this.childBuilderContexts = it
        }
        children.add(scope)
    }

    private fun detach() {
        parent.childBuilderContexts?.remove(this)
    }

    fun attach(parent: Scope) {
        this.parent = parent
        parent.addChild(this)
    }

    fun disposeChildren() {
        notifyDisposeListeners()
        childBuilderContexts?.forEach {
            it.detach()
            it.disposeChildren()
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