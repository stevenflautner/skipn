package io.skipn.builders

import io.skipn.observers.ClassesBuilder
import kotlinx.html.FlowContent

fun FlowContent.classes(classes: ClassesBuilder.() -> Unit) {
    attributes["class"] = ClassesBuilder().apply {
        classes()
    }.build()
}