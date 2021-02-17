package io.skipn.provide

import io.skipn.builder.buildContext
import io.skipn.prepareElement
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.FlowContent

actual inline fun <reified V: Any?, T: StateFlow<V>> FlowContent.pin(stateFlow: T): T {
    buildContext.pinningContext.pinStateFlow(this, stateFlow)
    return stateFlow
}

actual inline fun <reified T : Any> FlowContent.pin(instance: T): T {
    buildContext.pinningContext.pinInstance(this, instance)
    return instance
}