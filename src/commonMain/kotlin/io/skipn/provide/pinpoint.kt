package io.skipn.provide

import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import kotlinx.html.FlowContent

expect inline fun <reified V: Any?> FlowContent.pinpoint(statefulValue: StatefulValue<V>): StatefulValue<V>
expect inline fun <reified T: Stateful> FlowContent.pinpoint(stateful: T): T