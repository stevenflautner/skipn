package io.skipn.provide

import io.skipn.builder.buildContext
import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.FlowContent

inline fun <reified T: Any> FlowContent.locate(): T {
    return locateOrNull() ?: throw Exception(
        "No state of [${T::class}] was provided above this Component\n" +
        "Provide a stateful widget of the specified type as an ascendant"
    )
}

inline fun <reified T: Any> FlowContent.locateOrNull(): T? {
    return buildContext.pinningContext.findInstanceOrNull<T>() as? T
}

inline fun <reified V: Any?, T: StateFlow<V>> FlowContent.locateStateFlow(): T {
    return locateStateFlowOrNull<V, T>() ?: throw Exception(
        "No state of [${V::class}] was provided above this Component\n" +
        "Provide a stateful widget of the specified type as an ascendant"
    )
}

//inline fun <reified T: Any> FlowContent.locateByValueOrNull(): StatefulValue<T>? {
//    buildContext.pinningContext.findInstanceOrNull<T>()
////    val state = buildContext.pinnedClassMap[T::class]
////    return state as? StatefulValue<T>
//}

inline fun <reified V: Any?, T: StateFlow<V>?> FlowContent.locateStateFlowOrNull(): T? {
    val instance = buildContext.pinningContext.findInstanceOrNull<V>()
    return instance as? T
}