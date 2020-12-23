package io.skipn.observers

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker

@HtmlTagMarker
expect fun FlowContent.router(node: DIV.(String?) -> Unit)

@HtmlTagMarker
expect fun FlowContent.parameter(key: String, node: DIV.(String?) -> Unit)