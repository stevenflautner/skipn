package io.skipn.observers

import io.skipn.prepareElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.FlowContent

actual fun <T> FlowContent.textOf(
    stateFlow: StateFlow<T>,
    value: (T) -> String
) {
    prepareElement()

    consumer.onTagContent(value(stateFlow.value))
}

actual fun <T> FlowContent.textOf(flow: Flow<T>, value: () -> String) {
    prepareElement()

    consumer.onTagContent(value())
}