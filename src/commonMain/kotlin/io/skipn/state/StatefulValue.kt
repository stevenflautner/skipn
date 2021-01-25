package io.skipn.state

import kotlin.properties.Delegates

typealias Observer<T> = (T) -> Unit

interface StatefulValue<T> {
    val value: T

    fun observe(observer: Observer<T>): Observer<T>

    fun removeObserver(observer: Observer<T>)
}

class MutableStatefulValue<T>(value: T): StatefulValue<T> {

    protected val observers = mutableListOf<Observer<T>>()

    override var value: T by Delegates.observable(value) { _, _, newValue ->
        observers.forEach {
            it(newValue)
        }
    }

    override fun observe(observer: Observer<T>): Observer<T> {
        return observer.also {
            observers += observer
        }
    }

    override fun removeObserver(observer: Observer<T>) {
        observers -= observer
    }

    fun asStatefulValue() = this as StatefulValue<T>
}

fun <T1, T2, T> StatefulValue<T1>.combine(second: StatefulValue<T2>, transform: (T1, T2) -> T): StatefulValue<T> {
    val combined = mutableStatefulValueOf(transform(this.value, second.value))

    fun update() {
        combined.value = transform(this.value, second.value)
    }

    observe {
        update()
    }
    second.observe {
        update()
    }
    return combined
}

fun <T> mutableStatefulValueOf(value: T) = MutableStatefulValue(value)