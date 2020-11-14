package io.skipn.observers

import io.skipn.prepareElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.div

@HtmlTagMarker
actual fun <V, T> FlowContent.divOf(
    stateFlow: StateFlow<V>,
    node: DIV.(V) -> T
) {
    div {
        prepareElement()
        node(stateFlow.value)
    }
}

@HtmlTagMarker
actual fun FlowContent.divOf(
        flow: Flow<*>,
        node: DIV.() -> Unit
) {
    div {
        prepareElement()
        node()
    }
}