package io.skipn.observers

import io.skipn.builder.buildContext
import io.skipn.builder.currentRoute
import io.skipn.builder.parameter
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker

@HtmlTagMarker
fun FlowContent.router(node: DIV.(String?) -> Unit) {
    divOf(currentRoute) { route ->
        node(route)
    }
}

@HtmlTagMarker
fun FlowContent.parameter(key: String, node: DIV.(String?) -> Unit) {
    divOf(buildContext.parameter(key)) { parameter ->
        node(parameter)
    }
}