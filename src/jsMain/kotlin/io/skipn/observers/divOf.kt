package io.skipn.observers

import io.skipn.builder.BuildContext
import io.skipn.builder.buildContext
import io.skipn.builder.builder
import io.skipn.html.create
import io.skipn.prepareElement
import io.skipn.state.State
import io.skipn.state.Stream
import io.skipn.state.observeWithin
import kotlinx.browser.document
import kotlinx.html.*
import org.w3c.dom.Element

@HtmlTagMarker
actual fun <V, T> FlowContent.divOf(state: State<V>, node: DIV.(V) -> T) {
    div {
        var element = prepareElement()
        val parentContext = buildContext

        // Creates a new Build Context
        // as a copy of the current one
        val context = builder.createContextAndDescend(element.id)

        // Run node first
        node(state.value)

        state.observeWithin(parentContext.scope) { newValue ->
            context.scope.disposeChildren()

            element = replaceElement(element, context) {
                node(newValue)
            }
        }
    }
}

@HtmlTagMarker
actual fun <V, T> FlowContent.divOf(stream: Stream<V>, node: DIV.() -> T) {
    div {
        var element = prepareElement()
        val parentContext = buildContext

        // Creates a new Build Context
        // as a copy of the current one
        val context = builder.createContextAndDescend(element.id)

        // Run node first
        node()

        stream.observeWithin(parentContext.scope) {
            context.scope.disposeChildren()

            element = replaceElement(element, context) {
                node()
            }
        }
    }
}

//@HtmlTagMarker
//actual fun <V, T> FlowContent.divOf(stateFlow: StateFlow<V>, node: DIV.(V) -> T) {
//    div {
//        var element = prepareElement()
//        val parentScope = buildContext.getCoroutineScope()
//
//        // Creates a new Build Context
//        // as a copy of the current one
//        val context = builder.createContextAndDescend(element.id)
//
//        val currentValue = stateFlow.value
//
//        // Run node first
//        node(currentValue)
//
//        // Listen for changes and ignore
//        // first value emitted
//        parentScope.launch {
//            // It's possible that by the time this coroutine runs,
//            // the current value of the stateflow changed.
//            // Then don't drop the first value
//            val drop = if (currentValue == stateFlow.value) 1 else 0
//
//            stateFlow.drop(drop).collect { value ->
//                context.cancelAndCreateScope(parentScope)
//                context.getCoroutineScope().launch {
//                    element = replaceElement(element, context) {
//                        node(value)
//                    }
//                }
//            }
//        }
//    }
//}

//@HtmlTagMarker
//actual fun FlowContent.divOf(
//    flow: Flow<*>,
//    node: DIV.() -> Unit
//) {
//    div {
//        var element = prepareElement()
//        val parentScope = buildContext.getCoroutineScope()
//
//        // Creates a new Build Context
//        // as a copy of the current one
//        val context = builder.createContextAndDescend(element.id)
//
//        // Run node first
//        node()
//
//        // Listen for changes and ignore
//        // first value emitted
//        parentScope.launch {
//            val drop = (flow as? SharedFlow)?.replayCache?.size ?: 0
//            // Drop all values in the replay cache
//            // So we only notify newly emitted values
//            flow.drop(drop).collect {
//                context.cancelAndCreateScope(parentScope)
//                context.getCoroutineScope().launch {
//                    element = replaceElement(element, context) {
//                        node()
//                    }
//                }
//            }
//        }
//    }
//}

inline fun replaceElement(element: Element, context: BuildContext, crossinline node: DIV.() -> Unit): Element {
    val newElement = document.create(context).div {
        id = element.id

        node()
    }
    element.replaceWith(newElement)
    return newElement
}

//@HtmlTagMarker
//actual fun <T> FlowContent.divOf(flow: Flow<T>, initialValue: T, node: DIV.(T) -> Unit) {
//    divOf(flow.stateIn(buildContext.getCoroutineScope(), SharingStarted.Eagerly, initialValue), node)
//}