package io.skipn.notifiers

abstract class StatefulNotifier<T: Any> {
    val listeners = arrayListOf<T>()

    fun listen(listener: T) {
        listeners.add(listener)
    }

    abstract fun notifyListeners()
}

abstract class Stateful : StatefulNotifier<() -> Unit>() {
    override fun notifyListeners() {
        listeners.forEach {
            it.invoke()
        }
    }
}

open class StatefulValue<V: Any?>(default: V? = null) : StatefulNotifier<(V) -> Unit>() {
    internal var value: V? = null

    fun getValue() = value as V

    init {
        if (default != null) init(default)
    }

    internal fun init(default: V): StatefulValue<V> {
        value = default
        return this
    }

    fun setValue(value: V) {
        this.value = value
        notifyListeners()
    }

    override fun notifyListeners() {
        listeners.forEach {
            it.invoke(getValue())
        }
    }
}