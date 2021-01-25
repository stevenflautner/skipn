package io.skipn.observers

import io.skipn.prepareElement
import io.skipn.state.StatefulValue
import kotlinx.html.FlowContent
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement

private fun updateElement(element: Element, name: String, value: String) {
    when (element) {
        is HTMLInputElement -> {
            when(name) {
                "value" -> element.value = value
                "checked" -> {
                    element.checked = value == "true"
                }
                else -> element.setAttribute(name, value)
            }
        }
        else -> element.setAttribute(name, value)
    }
}

actual fun <T> FlowContent.attributeOf(
    name: String,
    statefulValue: StatefulValue<T>,
    value: (T) -> String
) {
    val element = prepareElement()

    updateElement(element, name, value(statefulValue.value))

    statefulValue.observe { newValue ->
        // TODO CHANGE THIS TO VALUEOF
        updateElement(element, name, value(newValue))
    }
}
//actual fun <T> FlowContent.attributeOf(
//    name: String,
//    stateFlow: StateFlow<T>,
//    value: (T) -> String
//) {
//    val element = prepareElement()
//
//    updateElement(element, name, value(stateFlow.value))
//
//    launch {
//        stateFlow.collect {
//            // TODO CHANGE THIS TO VALUEOF
//            updateElement(element, name, value(stateFlow.value))
//        }
//    }
//}
//
//actual fun <T> FlowContent.attributeOf(
//    name: String,
//    flow: Flow<T>,
//    value: () -> String
//) {
//    val element = prepareElement()
//
//    // TODO CHANGE THIS TO VALUEOF
//    updateElement(element, name, value())
//
//    launch {
//        flow.collect {
//            updateElement(element, name, value())
//        }
//    }
//}
//
//actual fun <T> FlowContent.attributeOf(name: String, flow: Flow<T>, initialValue: T, value: (T) -> String) {
//    attributeOf(name, flow.stateIn(buildContext.getCoroutineScope(), SharingStarted.Eagerly, initialValue), value)
//}