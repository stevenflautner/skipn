package io.skipn.state

import io.skipn.builder.BuildContext
import io.skipn.observers.Scope

interface Stream<T> {

    fun observe(observer: Observer<T>): Observer<T>

    fun removeObserver(observer: Observer<T>)

    fun emit(value: T)

}

fun <T> Stream<T>.observeWithin(scope: Scope, observer: Observer<T>): Observer<T> {
    observe(observer)
    scope.onDispose {
        removeObserver(observer)
    }
    return observer
}

open class MutableStream<T>: Stream<T> {

    protected val observers = mutableListOf<Observer<T>>()

    override fun observe(observer: Observer<T>): Observer<T> {
        return observer.also {
            observers += observer
        }
    }

    override fun removeObserver(observer: Observer<T>) {
        observers -= observer
    }

    override fun emit(value: T) {
        observers.forEach {
            it(value)
        }
    }

    fun asStream() = this as Stream<T>
}

interface Disposable {
    val dispose: () -> Unit
}

open class DisposableStream<T>(stream: MutableStream<T>, val dispose: () -> Unit): Stream<T> by stream

class DisposableState<T>(state: MutableState<T>, dispose: () -> Unit): DisposableStream<T>(state, dispose), State<T> {
    override val value = state.value
}

inline fun <T> Stream<T>.filter(crossinline predicate: (T) -> Boolean): DisposableStream<T> = transform { value ->
    if (predicate(value))
        return@transform emit(value)
}

inline fun <T, R> Stream<T>.transform(crossinline transform: Stream<R>.(value: T) -> Unit): DisposableStream<R> {
    val stream = streamOf<R>()
    val observer = observe {
        stream.transform(it)
    }
    return DisposableStream(stream) {
        removeObserver(observer)
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified R> Stream<*>.filterIsInstance(): DisposableStream<R> = filter { it is R } as DisposableStream<R>

fun <T: Any> Stream<T?>.filterNotNull(): Stream<T> = transform { value ->
    if (value != null) return@transform emit(value)
}

inline fun <T, R> Stream<T>.map(crossinline transform: (value: T) -> R): DisposableStream<R> = transform { value ->
    return@transform emit(transform(value))
}

internal class CONSTANT(val name: String) {
    override fun toString(): String = name
}

//private val NOT_YET_RECEIVED = CONSTANT("NOT_YET_RECEIVED")

fun <T1, T2, T> Stream<T1>.combine(secondStream: Stream<T2>, transform: (T1, T2) -> T): DisposableStream<T> {
    val combined = streamOf<T>()

    var firstValue: T1? = null
    var secondValue: T2? = null

    var firstValueReceived = false
    var secondValueReceived = false

    if (this is State<T1>) {
        firstValue = this.value
        firstValueReceived = true
    }
    if (secondStream is State<T2>) {
        secondValue = secondStream.value
        secondValueReceived = true
    }

    fun tryEmit() {
        if (firstValueReceived && secondValueReceived)
            combined.emit(transform(firstValue!!, secondValue!!))
    }

    val firstObserver = observe {
        firstValue = it
        tryEmit()
    }
    val secondObserver = secondStream.observe {
        secondValue = it
        tryEmit()
    }

    tryEmit()

    return DisposableStream(combined) {
        removeObserver(firstObserver)
        secondStream.removeObserver(secondObserver)
    }
}

fun <T> Stream<T>.toState(default: T): DisposableState<T> {
    val state = stateOf(default)
    val observer = observe {
        state.value = it
    }
    return DisposableState(state) {
        removeObserver(observer)
    }
}

//fun <T> DisposableState<T>.attach(scope: Scope): DisposableState<T> {
//    scope.onDispose {
//        dispose()
//    }
//    return this
//}

fun <V: DisposableStream<T>, T> V.attach(scope: Scope): V {
    scope.onDispose {
        dispose()
    }
    return this
}

fun <V: DisposableStream<T>, T> V.attach(context: BuildContext) = attach(context.scope)