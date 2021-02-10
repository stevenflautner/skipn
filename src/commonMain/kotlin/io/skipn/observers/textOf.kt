package io.skipn.observers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.FlowContent

expect fun <T: Any?> FlowContent.textOf(stateFlow: StateFlow<T>, value: (T) -> String)
expect fun <T: Any?> FlowContent.textOf(flow: Flow<T>, value: () -> String)