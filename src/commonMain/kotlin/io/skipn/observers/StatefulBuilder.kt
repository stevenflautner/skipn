package io.skipn.observers

import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker

//@HtmlTagMarker
//expect fun FlowContent.StatefulBuilder(predicate: StatefulBuilderPredicate, node: DIV.() -> Unit)
//@HtmlTagMarker
//expect fun <T: Any> FlowContent.StatefulBuilder(predicate: StatefulBuilderValuePredicate<T>, node: DIV.(T) -> Unit)
//@HtmlTagMarker
//expect fun <V: Any?, T> FlowContent.divOf(stateful: StatefulValue<V>, node: DIV.(V) -> T)

//@HtmlTagMarker
//expect fun <T: Any, R: Any> FlowContent.divOf(builder: StatefulFilterBuilder<T, R>, node: DIV.(R) -> Unit)

//@HtmlTagMarker
//expect fun FlowContent.divOf(stateful: Stateful, node: DIV.() -> Unit)