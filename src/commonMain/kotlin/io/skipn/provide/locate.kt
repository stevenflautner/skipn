package io.skipn.provide

import io.skipn.builder.buildContext
import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import kotlinx.html.FlowContent

inline fun <reified T: Stateful> FlowContent.locate(): T {
    return locateOrNull() ?: throw Exception(
        "No state of [${T::class}] was provided above this Component\n" +
        "Provide a stateful widget of the specified type as an ascendant"
    )
}

inline fun <reified T: Stateful> FlowContent.locateOrNull(): T? {
    val state = buildContext.statesByClasses[T::class]
    return state as? T
}

inline fun <reified T: Any> FlowContent.locateByValue(): StatefulValue<T> {
    return locateByValueOrNull() ?: throw Exception(
        "No state of [${T::class}] was provided above this Component\n" +
        "Provide a stateful widget of the specified type as an ascendant"
    )
}

inline fun <reified T: Any> FlowContent.locateByValueOrNull(): StatefulValue<T>? {
    val state = buildContext.statesByClasses[T::class]
    return state as? StatefulValue<T>
}