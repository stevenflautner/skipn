package io.skipn.provide

import kotlinx.html.FlowContent

//expect inline fun <reified V: Any?> FlowContent.pinpoint(statefulValue: StatefulValue<V>): StatefulValue<V>
//expect inline fun <reified T: Stateful> FlowContent.pinpoint(stateful: T): T