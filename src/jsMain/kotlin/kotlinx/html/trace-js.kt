package kotlinx.html

import kotlinx.html.*
import kotlinx.html.consumers.trace

fun <R> TagConsumer<R>.trace() : TagConsumer<R> = trace(println = { console.info(it) })
