package io.skipn.provide

import io.skipn.builder.buildContext
import io.skipn.prepareElement
import io.skipn.state.State
import kotlinx.html.FlowContent

actual inline fun <reified V: Any?, T: State<V>> FlowContent.pin(state: T): T {
    val id = prepareElement().id
    buildContext.pinningContext.pinState(id, state)
    return state
}

actual inline fun <reified T : Any> FlowContent.pin(instance: T): T {
    val id = prepareElement().id
    buildContext.pinningContext.pinInstance(id, instance)
    return instance
}