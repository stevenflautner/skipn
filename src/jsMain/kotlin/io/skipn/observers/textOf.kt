package io.skipn.observers

import io.skipn.builder.launch
import io.skipn.prepareElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.html.FlowContent
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement

private fun updateElement(element: Element, text: String) {
    element.textContent = text
}

actual fun <T> FlowContent.textOf(
    stateFlow: StateFlow<T>,
    value: (T) -> String
) {
    val element = prepareElement()

    updateElement(element, value(stateFlow.value))

    launch {
        stateFlow.collect {
            updateElement(element, value(stateFlow.value))
        }
    }
}

actual fun <T> FlowContent.textOf(flow: Flow<T>, value: () -> String) {
    val element = prepareElement()

    updateElement(element, value())

    launch {
        flow.collect {
            updateElement(element, value())
        }
    }
}