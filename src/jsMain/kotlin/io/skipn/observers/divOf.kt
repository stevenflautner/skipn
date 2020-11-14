package io.skipn.observers

import io.skipn.builder.BuildContext
import io.skipn.builder.buildContext
import io.skipn.builder.builder
import io.skipn.prepareElement
import kotlinx.browser.document
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.html.*
import kotlinx.html.dom.create
import org.w3c.dom.Element

@HtmlTagMarker
actual fun <V, T> FlowContent.divOf(stateFlow: StateFlow<V>, node: DIV.(V) -> T) {
    div {
        var element = prepareElement()
        val parentScope = buildContext.coroutineScope

        // Creates a new Build Context
        // as a copy of the current one
        val context = builder.createContextAndDescend(element.id)

        // Run node first
        node(stateFlow.value)

        // Listen for changes and ignore
        // first value emitted
        parentScope.launch {
            stateFlow.drop(1).collect { value ->
                // Cancel the current coroutine context
                // and replace it with a new one
                context.coroutineScope.cancel()

                // Create new coroutine scope
                // and rebuild the child tree with it
                context.coroutineScope = CoroutineScope(SupervisorJob(parentScope.coroutineContext.job))
                context.launch {
                    element = replaceElement(element, context) {
                        node(value)
                    }
                }
            }
        }
    }
}

@HtmlTagMarker
actual fun FlowContent.divOf(
    flow: Flow<*>,
    node: DIV.() -> Unit
) {
    div {
        var element = prepareElement()
        val parentScope = buildContext.coroutineScope

        // Creates a new Build Context
        // as a copy of the current one
        val context = builder.createContextAndDescend(element.id)

        // Run node first
        node()

        // Listen for changes and ignore
        // first value emitted
        parentScope.launch {
            val drop =
                if (flow is SharedFlow)
                    flow.replayCache.size
                else 0
            // Drop all values in the replay cache
            // So we only notify newly emitted values
            flow.drop(drop).collect {
                // Cancel the current coroutine context
                // and replace it with a new one
                context.coroutineScope.cancel()

                // Create new coroutine scope
                // and rebuild the child tree with it
                context.coroutineScope = CoroutineScope(SupervisorJob(parentScope.coroutineContext.job))
                context.coroutineScope.launch {
                    element = replaceElement(element, context) {
                        node()
                    }
                }
            }
        }
    }
}

private fun replaceElement(element: Element, context: BuildContext, node: DIV.() -> Unit): Element {
    val newElement = document.create(context).div {
        id = element.id

        node()
    }
    element.replaceWith(newElement)
    return newElement
}