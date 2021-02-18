package io.skipn.observers

import io.skipn.builder.buildContext
import io.skipn.builder.builder
import io.skipn.prepareElement
import io.skipn.skipnContext
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.div

@HtmlTagMarker
actual fun FlowContent.router(node: DIV.(String?) -> Unit) {
    div {
        val id = prepareElement()

        val route = skipnContext.router.routeFor(buildContext.routeLevel)

        builder.descendRoute(id)

        node(route)
    }
}