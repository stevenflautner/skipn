package io.skipn.observers

import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import io.skipn.prepareElement
import kotlinx.html.FlowContent

actual fun <V: Any?> FlowContent.classes(
    state: StatefulValue<V>,
    classes: (V) -> String
) {
    val element = prepareElement()

    state.listeners.add { value ->
        element.setAttribute("class", classes(value))
//        element.className = classes(value)
    }

    element.setAttribute("class", classes(state.getValue()))
//    element.className = classes(state.getValue())
}

actual fun FlowContent.classes(state: Stateful, classes: () -> String) {
    val element = prepareElement()

    state.listeners.add {
        element.className = classes()
    }

    element.className = classes()
}

actual fun FlowContent.attribute(
    name: String,
    stateful: Stateful,
    value: () -> String
) {
    val element = prepareElement()
    stateful.listen {
        element.setAttribute(name, value())
    }
    element.setAttribute(name, value())
}

actual fun <T> FlowContent.attribute(
    name: String,
    stateful: StatefulValue<T>,
    value: (T) -> String
) {
    val element = prepareElement()
    stateful.listen {
        element.setAttribute(name, value(it))
    }
    element.setAttribute(name, value(stateful.getValue()))
}

actual fun <T : Any, R : Any> FlowContent.attribute(
    name: String,
    builder: StatefulFilterBuilder<T, R>,
    value: (R) -> String
) {
    val element = prepareElement()
    builder.stateful.listen {
        val result = builder.runFilter()
        if (builder.conditionsPassed && result != null) {
            element.setAttribute(name, value(result))
        }
    }

    val result = builder.runFilter()
    if (result != null)
        element.setAttribute(name, value(result))
}