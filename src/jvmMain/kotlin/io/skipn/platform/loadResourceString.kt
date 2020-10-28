package io.skipn.platform

import kotlinx.html.FlowContent

actual fun loadResourceString(src: String): String {
    return FlowContent::class.java.classLoader.getResource(src)?.readText()
        ?: throw Exception("Resource does not exists at: $src")
//    val tagMatch = "<svg(.[^>]*)>(.*)</svg>"
//            .toRegex(RegexOption.DOT_MATCHES_ALL).matchEntire(svgFileString)
//            ?: throw Exception("File can not be interpreted as an svg at $src")
}