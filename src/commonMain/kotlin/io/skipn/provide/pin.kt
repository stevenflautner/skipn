package io.skipn.provide

import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.FlowContent

expect inline fun <reified V: Any?, T: StateFlow<V>> FlowContent.pin(stateFlow: T): T
expect inline fun <reified T: Any> FlowContent.pin(instance: T): T