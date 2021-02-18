@file:JvmMultifileClass
@file:JvmName("router_")

package io.skipn.observers

import io.skipn.builder.buildContext
import io.skipn.builder.builder
import io.skipn.builder.currentRoute
import io.skipn.builder.parameter
import io.skipn.skipnContext
import kotlinx.coroutines.launch
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.div
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@HtmlTagMarker
expect fun FlowContent.router(node: DIV.(String?) -> Unit)

@HtmlTagMarker
fun FlowContent.parameter(key: String, node: DIV.(String?) -> Unit) {
    divOf(buildContext.parameter(key)) { parameter ->
        node(parameter)
    }
}