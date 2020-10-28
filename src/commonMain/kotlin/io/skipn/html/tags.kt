@file:JvmMultifileClass
@file:JvmName("tags_")

package io.skipn.html

import kotlinx.html.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Document head
 */
@HtmlTagMarker
inline fun HTML.head(crossinline block : HEAD.() -> Unit = {}) : Unit = HEAD(kotlinx.html.emptyMap, consumer).visit {
    block()
    skipnHead()
}

expect fun HEAD.skipnHead()

/**
 * Document body
 */
@HtmlTagMarker
inline fun HTML.body(classes : String? = null, crossinline block : BODY.() -> Unit = {}) : Unit = BODY(attributesMapOf("class", classes), consumer).visit {
    block()
    skipnBody()
}

expect fun BODY.skipnBody()