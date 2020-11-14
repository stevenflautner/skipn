package io.skipn.provide

import io.skipn.builder.buildContext
import io.skipn.prepareElement
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.FlowContent

actual inline fun <reified V: Any?, T: StateFlow<V>> FlowContent.pin(stateFlow: T): T {
    val id = prepareElement().id
    buildContext.pinningContext.pinStateFlow(id, stateFlow)
    return stateFlow
}

actual inline fun <reified T : Any> FlowContent.pin(instance: T): T {
    val id = prepareElement().id
    buildContext.pinningContext.pinInstance(id, instance)
    return instance
}