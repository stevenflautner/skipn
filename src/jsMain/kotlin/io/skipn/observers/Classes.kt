package io.skipn.observers

import io.skipn.builder.buildContext
import io.skipn.builder.launch
import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import io.skipn.prepareElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.html.FlowContent

actual fun <V: Any?> FlowContent.classesOf(
    stateFlow: StateFlow<V>,
    classes: ClassesBuilder.(V) -> Unit
) {
    attributeOf("class", stateFlow) { value ->
        ClassesBuilder().apply {
            classes(value)
        }.build()
    }
}

//actual fun FlowContent.classes(state: Stateful, classes: () -> String) {
//    val element = prepareElement()
//
//    state.listeners.add {
//        element.className = classes()
//    }
//
//    element.className = classes()
//}

//actual fun FlowContent.attribute(
//    name: String,
//    stateful: Stateful,
//    value: () -> String
//) {
//    val element = prepareElement()
//    stateful.listen {
//        element.setAttribute(name, value())
//    }
//    element.setAttribute(name, value())
//}

actual fun <T> FlowContent.attributeOf(
    name: String,
    stateFlow: StateFlow<T>,
    value: (T) -> String
) {
    val element = prepareElement()

    element.setAttribute(name, value(stateFlow.value))

    launch {
        stateFlow.collect {
            element.setAttribute(name, value(it))
        }
    }
}

//actual fun <T : Any, R : Any> FlowContent.attribute(
//    name: String,
//    builder: StatefulFilterBuilder<T, R>,
//    value: (R) -> String
//) {
//    val element = prepareElement()
//    builder.stateful.listen {
//        val result = builder.runFilter()
//        if (builder.conditionsPassed && result != null) {
//            element.setAttribute(name, value(result))
//        }
//    }
//
//    val result = builder.runFilter()
//    if (result != null)
//        element.setAttribute(name, value(result))
//}
actual fun <V> FlowContent.classesOf(flow: Flow<V>, classes: ClassesBuilder.() -> Unit) {
    attributeOf("class", flow) {
        ClassesBuilder().apply(classes).build()
    }
}

actual fun <T> FlowContent.attributeOf(
    name: String,
    flow: Flow<T>,
    value: () -> String
) {
    val element = prepareElement()

    element.setAttribute(name, value())

    launch {
        flow.collect {
            element.setAttribute(name, value())
        }
    }
}