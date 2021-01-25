package io.skipn.state

import kotlin.properties.Delegates

typealias Observer<T> = (T) -> Unit

interface StatefulValue<out T> {
    val value: T
}

open class MutableStatefulValue<T>(value: T): StatefulValue<T> {

    protected val observers = mutableListOf<Observer<T>>()

    override var value: T by Delegates.observable(value) { _, _, newValue ->
        observers.forEach {
            it(newValue)
        }
    }

    fun observe(observer: Observer<T>): Observer<T> {
        return observer.also {
            observers += observer
        }
    }

    fun removeObserver(observer: Observer<T>) {
        observers -= observer
    }

    fun asStatefulValue() = this as StatefulValue<T>
}