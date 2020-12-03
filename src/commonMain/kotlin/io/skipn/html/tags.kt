@file:JvmMultifileClass
@file:JvmName("tags_")

package io.skipn.html

import kotlinx.html.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

///**
// * Document head
// */
//@HtmlTagMarker
//inline fun HTML.head(crossinline block : HEAD.() -> Unit = {}) : Unit = HEAD(kotlinx.html.emptyMap, consumer).visit {
//    block()
//    skipnHead()
//}

expect fun HEAD.skipnHead()

///**
// * Document body
// */
//@HtmlTagMarker
//inline fun HTML.body(classes : String? = null, crossinline block : BODY.() -> Unit = {}) : Unit = BODY(attributesMapOf("class", classes), consumer).visit {
//    block()
//    skipnBody()
//}

expect fun BODY.skipnBody()

class HtmlApp(var head: (HEAD.() -> Unit)? = null, var body: (BODY.() -> Unit)? = null)

typealias App = HtmlApp.() -> Unit

fun HTML.html(app: App) {
    val htmlApp = HtmlApp().apply(app)

    head {
        htmlApp.head?.invoke(this)
        skipnHead()
    }
    body {
        htmlApp.body?.invoke(this)
        skipnBody()
    }
}
@HtmlTagMarker
fun HtmlApp.head(head: HEAD.() -> Unit) {
    this.head = head
}
@HtmlTagMarker
fun HtmlApp.body(body: BODY.() -> Unit) {
    this.body = body
}