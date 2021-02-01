package io.skipn.provide

import io.skipn.state.State
import kotlinx.html.FlowContent

expect inline fun <reified V: Any?, T: State<V>> FlowContent.pin(state: T): T
expect inline fun <reified T: Any> FlowContent.pin(instance: T): T