package io.skipn.observers

import io.skipn.notifiers.StatefulValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker

@HtmlTagMarker
expect fun <V: Any?, T> FlowContent.divOf(stateFlow: StateFlow<V>, node: DIV.(V) -> T)

@HtmlTagMarker
expect fun FlowContent.divOf(flow: Flow<*>, node: DIV.() -> Unit)

