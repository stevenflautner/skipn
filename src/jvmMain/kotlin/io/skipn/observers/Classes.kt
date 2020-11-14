package io.skipn.observers

import io.skipn.prepareElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker

//actual fun FlowContent.classes(state: Stateful, classes: () -> String) {
//    prepareElement()
//    attributes["class"] = classes()
//}

actual fun <T> FlowContent.attributeOf(
    name: String,
    stateFlow: StateFlow<T>,
    value: (T) -> String
) {
    prepareElement()
    attributes[name] = value(stateFlow.value)
}

//actual fun FlowContent.attribute(
//    name: String,
//    stateful: Stateful,
//    value: () -> String
//) {
//    prepareElement()
//    attributes[name] = value()
//}
//
//actual fun <T : Any, R : Any> FlowContent.attribute(
//    name: String,
//    builder: StatefulFilterBuilder<T, R>,
//    value: (R) -> String
//) {
//    prepareElement()
//
//    val result = builder.runFilter()
//    if (result != null)
//        attributes[name] = value(result)
//}
actual fun <T> FlowContent.attributeOf(
    name: String,
    flow: Flow<T>,
    value: () -> String
) {
    prepareElement()
    attributes[name] = value()
}

actual fun <V> FlowContent.classesOf(flow: Flow<V>, classes: ClassesBuilder.() -> Unit) {
    attributeOf("class", flow) {
        ClassesBuilder().apply(classes).build()
    }
}

actual fun <V: Any?> FlowContent.classesOf(stateFlow: StateFlow<V>, classes: ClassesBuilder.(V) -> Unit) {
    attributeOf("class", stateFlow) { value ->
        ClassesBuilder().apply {
            classes(value)
        }.build()
    }
}