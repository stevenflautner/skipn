package io.skipn.provide

import io.skipn.builder.BuildContext
import io.skipn.builder.buildContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.FlowContent

//inline fun <reified T: Any> FlowContent.locate() = buildContext.locate<T>()
//inline fun <reified T: Any> FlowContent.locateOrNull() = buildContext.locateOrNull<T>()
//inline fun <reified V: Any?, T: StateFlow<V>> FlowContent.locateStateFlow() = buildContext.locateStateFlow<V, T>()
//inline fun <reified V: Any?, T: StateFlow<V>?> FlowContent.locateStateFlowOrNull() = buildContext.locateStateFlowOrNull<V, T>()


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

inline fun <reified V: Any?, T: StateFlow<V>?> FlowContent.locateStateFlowOrNull(): T? {
    val instance = buildContext.pinningContext.findInstanceOrNull<V>()
    return instance as? T
}

//inline fun <reified T: Any> FlowContent.locate() = buildContext.locate<T>()
//inline fun <reified T: Any> FlowContent.locateOrNull() = buildContext.locateOrNull<T>()
//inline fun <reified V: Any?, T: StateFlow<V>> FlowContent.locateStateFlow() = buildContext.locateStateFlow<V, T>()
//inline fun <reified V: Any?, T: StateFlow<V>?> FlowContent.locateStateFlowOrNull() = buildContext.locateStateFlowOrNull<V, T>()
//
//
//inline fun <reified T: Any> BuildContext.locate(): T {
//    return locateOrNull() ?: throw Exception(
//            "No state of [${T::class}] was provided above this Component\n" +
//                    "Provide a stateful widget of the specified type as an ascendant"
//    )
//}
//
//inline fun <reified T: Any> BuildContext.locateOrNull(): T? {
//    return pinningContext.findInstanceOrNull<T>() as? T
//}
//
//inline fun <reified V: Any?, T: StateFlow<V>> BuildContext.locateStateFlow(): T {
//    return locateStateFlowOrNull<V, T>() ?: throw Exception(
//            "No state of [${V::class}] was provided above this Component\n" +
//                    "Provide a stateful widget of the specified type as an ascendant"
//    )
//}
//
//inline fun <reified V: Any?, T: StateFlow<V>?> BuildContext.locateStateFlowOrNull(): T? {
//    val instance = pinningContext.findInstanceOrNull<V>()
//    return instance as? T
//}