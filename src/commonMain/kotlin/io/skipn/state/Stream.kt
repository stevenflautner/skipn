package io.skipn.state

import io.skipn.builder.BuildContext
import io.skipn.observers.Scope

interface Stream<T> {

    val lastEmitted: T
    val cacheLast: Boolean

    fun observe(observer: Observer<T>): Observer<T>
    fun collect(observer: Observer<T>): Observer<T>

    fun removeObserver(observer: Observer<T>)

    fun emit(value: T)

    val isCachingAndSet: Boolean

}

fun <T> Stream<T>.observeWithin(scope: Scope, observer: Observer<T>): Observer<T> {
    scope.onDispose {
        removeObserver(observer)
    }
    observe(observer)
    return observer
}

class CHECK

private val NOT_SET = CHECK()

open class MutableStream<T>(
    override val cacheLast: Boolean = false,
): Stream<T> {

    val observers = mutableListOf<Observer<T>>()

    private var lastEmittedValue: Any? = NOT_SET

    override val lastEmitted: T
        get() = lastEmittedValue as T

    private var sharing: Sharing? = null

    override val isCachingAndSet: Boolean
            get() = cacheLast && lastEmittedValue !== NOT_SET

    class Sharing(
        val start: () -> Unit,
        val stop: () -> Unit
    )

    fun setupObservation(start: () -> Unit, stop: () -> Unit) {
        sharing = Sharing(start, stop)
    }

    override fun observe(observer: Observer<T>): Observer<T> {
        observers += observer
        if (observers.size == 1)
            sharing?.start?.invoke()
        return observer
    }

    override fun collect(observer: Observer<T>): Observer<T> {
        observe(observer)
        if (isCachingAndSet) {
            observer(lastEmitted)
        }
        return observer
    }

    override fun removeObserver(observer: Observer<T>) {
        observers -= observer
        if (observers.size == 0)
            sharing?.stop?.invoke()
    }

    override fun emit(value: T) {
        // Equality check
        val lastEmittedValue = lastEmittedValue
        if (cacheLast) {
            this.lastEmittedValue = value
        }
        println("NEW VALUE")
        println(value)
        println(lastEmittedValue)
        println(value == lastEmittedValue)
        println(value === lastEmittedValue)
        if (value == lastEmittedValue)
            return
        observers.forEach {
            it(value)
        }
    }

    fun asStream() = this as Stream<T>
}

open class DisposableStream<T>(stream: MutableStream<T>, val dispose: () -> Unit): Stream<T> by stream

class DisposableState<T>(state: MutableState<T>, dispose: () -> Unit): DisposableStream<T>(state, dispose), State<T> {
    override val value = state.value
}

inline fun <T> Stream<T>.filter(crossinline predicate: (T) -> Boolean): Stream<T> = transform { value ->
    if (predicate(value))
        return@transform emit(value)
}

inline fun <T, R> Stream<T>.transform(crossinline transform: Stream<R>.(value: T) -> Unit): Stream<R> {
    val stream = streamOf<R>(cacheLast)

    var observer: Observer<T>? = null
    stream.setupObservation(
        start = {
            observer = observe {
                stream.transform(it)
            }
        },
        stop = {
            observer?.let {
                removeObserver(it)
                observer = null
            }
        }
    )

    if (isCachingAndSet) {
        stream.transform(lastEmitted)
    }

    return stream
}

@Suppress("UNCHECKED_CAST")
inline fun <reified R> Stream<*>.filterIsInstance(): Stream<R> = filter { it is R } as Stream<R>

fun <T: Any> Stream<T?>.filterNotNull(): Stream<T> = transform { value ->
    if (value != null) return@transform emit(value)
}

inline fun <T, R> Stream<T>.map(crossinline transform: (value: T) -> R): Stream<R> = transform { value ->
    return@transform emit(transform(value))
}

fun <T1, T2, T> Stream<T1>.combine(secondStream: Stream<T2>, transform: (T1, T2) -> T): Stream<T> {
    val combined = streamOf<T>(true)

    var firstValue: Any? = NOT_SET
    var secondValue: Any? = NOT_SET

    if (this.isCachingAndSet) {
        firstValue = lastEmitted
    }
    if (secondStream.isCachingAndSet) {
        secondValue = secondStream.lastEmitted
    }

    fun tryEmit() {
        if (firstValue !== NOT_SET && secondValue !== NOT_SET) {
            combined.emit(transform(firstValue as T1, secondValue as T2))
        }
    }

    var firstObserver: Observer<T1>? = null
    var secondObserver: Observer<T2>? = null

    combined.setupObservation(
        start = {
            firstObserver = observe {
                firstValue = it
                tryEmit()
            }
            secondObserver = secondStream.observe {
                secondValue = it
                tryEmit()
            }
        },
        stop = {
            firstObserver?.let {
                removeObserver(it)
                firstObserver = null
            }
            secondObserver?.let {
                secondStream.removeObserver(it)
                secondObserver = null
            }
        }
    )

    tryEmit()

    return combined
}

fun <T> Stream<T>.toState(default: T): State<T> {
    val state = stateOf(default)
    var observer: Observer<T>? = null
    state.setupObservation(
        start = {
            observer = observe {
                state.value = it
            }
        },
        stop = {
            observer?.let {
                removeObserver(it)
                observer = null
            }
        }
    )
    return state
}

fun <V: DisposableStream<T>, T> V.attach(scope: Scope): V {
    scope.onDispose {
        dispose()
    }
    return this
}

fun <V: DisposableStream<T>, T> V.attach(context: BuildContext) = attach(context.scope)