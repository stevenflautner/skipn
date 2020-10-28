package io.skipn.observers

import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import kotlinx.html.FlowContent

expect fun <V: Any?> FlowContent.classes(state: StatefulValue<V>, classes: (V) -> String)
expect fun FlowContent.classes(state: Stateful, classes: () -> String)

expect fun <T: Any?> FlowContent.attribute(name: String, stateful: StatefulValue<T>, value: (T) -> String)
expect fun FlowContent.attribute(name: String, stateful: Stateful, value: () -> String)
expect fun <T: Any, R: Any> FlowContent.attribute(name: String, builder: StatefulFilterBuilder<T, R>, value: (R) -> String)