package io.skipn.state

import io.skipn.builder.BuildContext
import kotlin.properties.Delegates

typealias Observer<T> = (T) -> Unit

interface State<T> : Stream<T> {
    val value: T
}

//class DisposableState<T>(statefulValue: MutableState<T>, val dispose: () -> Unit): State<T> by statefulValue {

//    override val value: T = statefulValue.value
//
//}

// Mutable stream that caches last value
class MutableState<T>(value: T): MutableStream<T>(cacheLast = true), State<T> {

//    override var value: T by Delegates.observable(value) { _, _, newValue ->
//        emit(newValue)
//    }
    init {
        emit(value)
    }

    override var value: T
        get() = lastEmitted
        set(value) {
            emit(value)
        }

    fun asState() = this as State<T>

}
//fun <T1, T2, T> State<T1>.combine(t2: State<T2>, transform: (T1, T2) -> T): DisposableState<T> {
//    val combined = stateOf(transform(this.value, t2.value))
//
//    fun update() {
//        combined.value = transform(this.value, t2.value)
//    }
//
//    val t1Observer = observe {
//        update()
//    }
//    val t2Observer = t2.observe {
//        update()
//    }
//
//    return DisposableState(combined) {
//        removeObserver(t1Observer)
//        t2.removeObserver(t2Observer)
//    }
//}

fun <T> stateOf(value: T) = MutableState(value)
fun <T> streamOf(cacheLast: Boolean = false) = MutableStream<T>(cacheLast)