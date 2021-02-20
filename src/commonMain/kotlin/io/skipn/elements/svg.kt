package io.skipn.elements

import io.skipn.*
import io.skipn.platform.DEV
import io.skipn.platform.Platform
import io.skipn.platform.loadResourceString
import io.skipn.platform.platform
import kotlinx.html.*

@HtmlTagMarker
fun FlowContent.svgFile(src: String, classes: String? = null, body: (SVG.() -> Unit)? = null) {
//    span(classes = classes) {
//        unsafe {
//            val fileString = loadResourceString(src)
//            raw(fileString)
//        }
//    }
    svg {
        try {
            if (skipnContext.isInitializing && platform == Platform.BROWSER && !DEV) {
                body?.invoke(this)
            } else {
                val fileString = loadResourceString(src)

                // NAMED GROUPS ARE NOT SUPPORTED IN SOME BROWSERS DON'T USE THEM
                val tagMatch = """[\s\S]*<svg([^>]*)>([\s\S]*)</svg>[\s\S]*"""
                    .toRegex(RegexOption.MULTILINE).matchEntire(fileString)
                    ?: throw Exception("File can not be interpreted as an svg at $src")

                val (svgAttrs, svgNoTag) = tagMatch.destructured

                // NAMED GROUPS ARE NOT SUPPORTED IN SOME BROWSERS DON'T USE THEM
                val attrsMatch = """(?:\s+(?:(\w+)=["']([^"'<>]+)["']))"""
                    .toRegex().findAll(svgAttrs)

                attrsMatch.forEach {
                    val (key, value) = it.destructured
                    if (key == "id" || key == "xmlns") return@forEach
                    attributes[key] = value
                }

                if (classes != null) {
                    attributes["class"] = classes
                }

                body?.invoke(this)

                unsafe {
                    raw(svgNoTag)
                }
            }
        } catch (e: Exception) {

        }
    }
}