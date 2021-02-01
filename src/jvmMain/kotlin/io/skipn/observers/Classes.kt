package io.skipn.observers

import io.skipn.prepareElement
import io.skipn.state.State
import io.skipn.state.Stream
import kotlinx.html.FlowContent

actual fun <T> FlowContent.attributeOf(
    name: String,
    state: State<T>,
    value: (T) -> String
) {
    prepareElement()
    attributes[name] = value(state.value)
}

actual fun <T> FlowContent.attributeOf(
    name: String,
    stream: Stream<T>,
    value: () -> String
) {
    prepareElement()
    attributes[name] = value()
}

//actual fun <T> FlowContent.attributeOf(
//    name: String,
//    stateFlow: StateFlow<T>,
//    value: (T) -> String
//) {
//    prepareElement()
//    attributes[name] = value(stateFlow.value)
//}
//
//actual fun <T> FlowContent.attributeOf(
//    name: String,
//    flow: Flow<T>,
//    value: () -> String
//) {
//    // TOOD SET CHECKED STATE SO THAT PRERENDERS WITH A DEFAULT STATE
//    prepareElement()
//    attributes[name] = value()
//}
//
//actual fun <T> FlowContent.attributeOf(name: String, flow: Flow<T>, initialValue: T, value: (T) -> String) {
//    prepareElement()
//    attributes[name] = value(initialValue)
//}