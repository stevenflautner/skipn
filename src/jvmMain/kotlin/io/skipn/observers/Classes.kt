package io.skipn.observers

import io.skipn.prepareElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.FlowContent

actual fun <T> FlowContent.attributeOf(
    name: String,
    stateFlow: StateFlow<T>,
    value: (T) -> String
) {
    prepareElement()
    attributes[name] = value(stateFlow.value)
}

actual fun <T> FlowContent.attributeOf(
    name: String,
    flow: Flow<T>,
    value: () -> String
) {
    // TOOD SET CHECKED STATE SO THAT PRERENDERS WITH A DEFAULT STATE
    prepareElement()
    attributes[name] = value()
}

actual fun <T> FlowContent.attributeOf(name: String, flow: Flow<T>, initialValue: T, value: (T) -> String) {
    prepareElement()
    attributes[name] = value(initialValue)
}