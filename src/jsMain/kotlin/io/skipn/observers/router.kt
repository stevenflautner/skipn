package io.skipn.observers

import io.skipn.builder.buildContext
import io.skipn.builder.builder
import io.skipn.prepareElement
import io.skipn.skipnContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.div

@HtmlTagMarker
actual fun FlowContent.router(node: DIV.(String?) -> Unit) {
    div {
        var element = prepareElement()
        val parentContext = buildContext
        val parentScope = parentContext.getCoroutineScope()

        val route = skipnContext.router.routeFor(parentContext.getRouteLevel())

        // Creates a new Build Context
        // as a copy of the current one
        val context = builder.createContextAndDescend(element.id, parentContext.getRouteLevel() + 1)

        // Run node first
        node(route)

        // Listen for changes of current route level
        parentScope.launch {
            skipnContext.router.filterRouteChangesFor(parentContext.getRouteLevel()).collect { change ->
                context.cancelAndCreateScope(parentScope)
                context.getCoroutineScope().launch {
                    element = replaceElement(element, context) {
                        node(change)
                    }
                }
            }
        }
    }
}

@HtmlTagMarker
actual fun FlowContent.parameter(key: String, node: DIV.(String?) -> Unit) {
    div {
        var element = prepareElement()
        val parentContext = buildContext
        val parentScope = parentContext.getCoroutineScope()

        val route = skipnContext.router.getParameterValue(key)

        // Creates a new Build Context
        // as a copy of the current one
        val context = builder.createContextAndDescend(element.id)

        // Run node first
        node(route)

        // Listen for changes of current route level
        parentScope.launch {
            skipnContext.router.filterParameterChangesFor(key).collect { change ->
                context.cancelAndCreateScope(parentScope)
                context.getCoroutineScope().launch {
                    element = replaceElement(element, context) {
                        node(change)
                    }
                }
            }
        }
    }
}