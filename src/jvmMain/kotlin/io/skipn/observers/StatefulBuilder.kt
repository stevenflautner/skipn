package io.skipn.observers

import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import io.skipn.prepareElement
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.div

//@HtmlTagMarker
//actual fun FlowContent.StatefulBuilder(
//    predicate: StatefulBuilderPredicate,
//    node: DIV.() -> Unit
//) {
//    div {
//        prepareElement()
//        if (predicate.predicate())
//            node()
//    }
//}
//
//@HtmlTagMarker
//actual fun <T : Any> FlowContent.StatefulBuilder(
//    predicate: StatefulBuilderValuePredicate<T>,
//    node: DIV.(T) -> Unit
//) {
//    div {
//        prepareElement()
//        if (predicate.predicate())
//            node(predicate.stateful.getValue())
//    }
//}
@HtmlTagMarker
actual fun <V: Any?, T> FlowContent.divOf(
        stateful: StatefulValue<V>,
        node: DIV.(V) -> T
) {
    div {
        prepareElement()
        node(stateful.getValue())
    }
}

@HtmlTagMarker
actual fun FlowContent.divOf(stateful: Stateful, node: DIV.() -> Unit) {
    div {
        prepareElement()
        node()
    }
}

//@HtmlTagMarker
//actual fun <T : Any, D> FlowContent.Stateful1Builder(
//    state: StatefulBuilderValuePredicate1<T, D>,
//    node: DIV.(D) -> Unit
//) {
//    div {
//        prepareElement()
//        node(state.predicate(state.stateful.getValue()))
//    }
//}

@HtmlTagMarker
actual fun <T: Any, R: Any> FlowContent.divOf(
    builder: StatefulFilterBuilder<T, R>,
    node: DIV.(R) -> Unit
) {
    div {
        prepareElement()

        val result = builder.runFilter()
        if (result != null)
            node(result)
    }
}