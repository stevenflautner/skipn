package io.skipn.observers

class Scope {
    var children: ArrayList<Scope>? = null
    private var disposeListeners: ArrayList<() -> Unit>? = null
    private lateinit var parent: Scope

    private fun addChild(scope: Scope) {
        val children = children ?: arrayListOf<Scope>().also {
            this.children = it
        }
        children.add(scope)
    }

    fun attach(parent: Scope) {
        this.parent = parent
        parent.addChild(this)
    }

    fun disposeChildren() {
        notifyDisposeListeners()
        children?.let { children ->
            children.forEach {
                it.disposeChildren()
            }
            children.clear()
            this.children = null
        }
    }

    private fun notifyDisposeListeners() {
        disposeListeners?.let { disposeListeners ->
            disposeListeners.forEach {
                it()
            }
            disposeListeners.clear()
            this.disposeListeners = null
        }
    }

    fun onDispose(onDispose: () -> Unit) {
        val disposeListeners = disposeListeners ?: arrayListOf<() -> Unit>().also {
            this.disposeListeners = it
        }
        disposeListeners.add(onDispose)
    }
}