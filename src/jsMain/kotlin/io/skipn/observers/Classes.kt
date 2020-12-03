package io.skipn.observers

import io.skipn.builder.buildContext
import io.skipn.builder.launch
import io.skipn.prepareElement
import kotlinx.coroutines.flow.*
import kotlinx.html.FlowContent
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

actual fun <T> FlowContent.attributeOf(
    name: String,
    stateFlow: StateFlow<T>,
    value: (T) -> String
) {
    val element = prepareElement()

//    element.setAttribute(name, value(stateFlow.value))

    if (name == "value" && element is HTMLInputElement)
        element.value = value(stateFlow.value)
    else
        element.setAttribute(name, value(stateFlow.value))

    launch {
        stateFlow.collect {
//            element.setAttribute(name, value(it))

            // TODO CHANGE THIS TO VALUEOF
            if (name == "value" && element is HTMLInputElement)
                element.value = value(it)
            else
                element.setAttribute(name, value(it))
        }
    }
}

actual fun <T> FlowContent.attributeOf(
    name: String,
    flow: Flow<T>,
    value: () -> String
) {
    val element = prepareElement()

    // TODO CHANGE THIS TO VALUEOF
    if (name == "value" && element is HTMLInputElement)
        element.value = value()
    else
        element.setAttribute(name, value())

    launch {
        flow.collect {
            if (name == "value" && element is HTMLInputElement)
                element.value = value()
            else
                element.setAttribute(name, value())
//            element.setAttribute(name, value())
        }
    }
}

actual fun <T> FlowContent.attributeOf(name: String, flow: Flow<T>, initialValue: T, value: (T) -> String) {
    attributeOf(name, flow.stateIn(buildContext.coroutineScope, SharingStarted.Eagerly, initialValue), value)
}