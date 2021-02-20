@file:JvmMultifileClass
@file:JvmName("router_")

package io.skipn.observers

import io.skipn.builder.buildContext
import io.skipn.builder.parameter
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.div
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

//@HtmlTagMarker
//expect fun FlowContent.router(node: DIV.(String?) -> Unit)

@HtmlTagMarker
fun FlowContent.parameter(key: String, node: DIV.(String?) -> Unit) {
    div {
        val parameter = dependOn { buildContext.parameter(key) }
        node(parameter)
    }
}