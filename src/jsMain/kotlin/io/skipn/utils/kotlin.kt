package io.skipn.utils

import kotlinx.browser.document
import org.w3c.dom.Element

@JsName("require")
external fun require_(module: String): dynamic

fun require(module: String): dynamic {
    return require_("../../../../processedResources/browser/main/$module")
}

fun byId(id: String): Element {
    val elem = document.getElementById(id)
    return elem ?: throw Exception("No HTMLElement found by id: $id")
}

//fun onClick(id: String, onClick: (HTMLButtonElement, MouseEvent) -> Unit) {
//    val button = byId(id) as HTMLButtonElement
//    button.onclick = {
//        onClick(button, it)
//    }
//}