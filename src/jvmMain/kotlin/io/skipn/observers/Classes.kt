package io.skipn.observers

import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import io.skipn.prepareElement
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker

@HtmlTagMarker
actual fun <V: Any?> FlowContent.classes(state: StatefulValue<V>, classes: (V) -> String) {
    prepareElement()
    attributes["class"] = classes(state.getValue())
}

actual fun FlowContent.classes(state: Stateful, classes: () -> String) {
    prepareElement()
    attributes["class"] = classes()
}

actual fun <T> FlowContent.attribute(
    name: String,
    stateful: StatefulValue<T>,
    value: (T) -> String
) {
    prepareElement()
    attributes[name] = value(stateful.getValue())
}

actual fun FlowContent.attribute(
    name: String,
    stateful: Stateful,
    value: () -> String
) {
    prepareElement()
    attributes[name] = value()
}

actual fun <T : Any, R : Any> FlowContent.attribute(
    name: String,
    builder: StatefulFilterBuilder<T, R>,
    value: (R) -> String
) {
    prepareElement()

    val result = builder.runFilter()
    if (result != null)
        attributes[name] = value(result)
}