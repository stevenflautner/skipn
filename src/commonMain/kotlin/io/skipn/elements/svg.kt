package io.skipn.elements

import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker
import kotlinx.html.SVG
import kotlinx.html.htmlObject

@HtmlTagMarker
fun FlowContent.svgFile(src: String, classes: String? = null, body: (SVG.() -> Unit)? = null) {
    htmlObject(classes = classes) {
        type = "image/svg+xml"
        data = "/$src"
//        + """Kiwi Logo <!-- fallback image in CSS -->"""
    }
//    svg {
//        if (skipnContext.isInitializing && platform == Platform.BROWSER && !DEV) {
//            body?.invoke(this)
//        } else {
//            val fileString = loadResourceString(src)
//
//            val tagMatch = """[\s\S]*<svg([^>]*)>([\s\S]*)</svg>[\s\S]*"""
//                .toRegex(RegexOption.MULTILINE).matchEntire(fileString)
//                ?: throw Exception("File can not be interpreted as an svg at $src")
//
//            val (svgAttrs, svgNoTag) = tagMatch.destructured
//
//            val attrsMatch = """(?:\s+(?:(\w+)=["'](?<class>[^"'<>]+)["']))"""
//                .toRegex().findAll(svgAttrs)
//
//            attrsMatch.forEach {
//                val (key, value) = it.destructured
//                if (key == "id" || key == "xmlns") return@forEach
//                attributes[key] = value
//            }
//
//            if (classes != null) {
//                attributes["class"] = classes
//            }
//
//            body?.invoke(this)
//
//            unsafe {
//                raw(svgNoTag)
//            }
//        }
//    }
}