@file:JvmMultifileClass
@file:JvmName("divOf_")

package io.skipn.observers

import io.skipn.state.StatefulValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@HtmlTagMarker
expect fun <V: Any?, T> FlowContent.divOf(statefulValue: StatefulValue<V>, node: DIV.(V) -> T)

//@HtmlTagMarker
//expect fun FlowContent.divOf(flow: Flow<*>, node: DIV.() -> Unit)
//
//@HtmlTagMarker
//expect fun <T: Any?> FlowContent.divOf(flow: Flow<T>, initialValue: T, node: DIV.(T) -> Unit)

//@HtmlTagMarker
//fun <T: Any?, R: Any?> FlowContent.divOf(stateFlowDef: StateFlowDef<T, R>, node: DIV.(R) -> Unit) {
//    with(stateFlowDef) {
//        divOf(parentStateFlow.map { transform(it) }, initialValue = stateFlowDef.value, node)
//    }
//}