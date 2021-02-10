package io.skipn.html

import io.skipn.builder.builder
import io.skipn.ensureRunAfterInitialization
import io.skipn.platform.DEV
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.appendElement
import kotlinx.html.*
import org.w3c.dom.HTMLHeadElement

actual fun BODY.skipnBody() {
    if (DEV) {
//        script {
//            src = "vimpexdrink.js"
//            attributes["id"] = "skipn-main-script"
//            attributes["data-dev"] = "true"
//        }
//        script {
//            attributes["id"] = "skipn-res"
//            attributes["data-res"] = ""
//        }
    }
}

actual fun HEAD.skipnHead() {
    if (DEV) {
        meta { charset = "utf-8" }
        meta { name = "viewport"; content = "width=device-width, initial-scale=1" }
    }
}

/**
 * Scripts are not included in the server prerender.
 * If you add a script to the Head, it will be appended
 * to the DOM's head once the framework has initialized,
 * therefore scripts will load afterwards.
 *
 * Server ignores the script tag defined
 * Browser should append it to the head.
 */
@HtmlTagMarker
actual inline fun FlowOrMetaDataOrPhrasingContent.script(
    type: String?,
    src: String?,
    crossinline block: SCRIPT.() -> Unit
) {
    val context = builder.rootBuildContext

    ensureRunAfterInitialization {
        document.head?.let { head ->
            head.append(document.create(context).script {
                block()
            })
        }
    }
}

//inline fun HEAD.scripta(
//    type: String?,
//    src: String?,
//    crossinline block: SCRIPT.() -> Unit
//) {
//    ensureRunAfterInitialization {
//        val context = builder.rootBuildContext
//
//        document.head?.let { head ->
//            head.append(document.create(context).script {
//                this()
//            })
//            document.head!!.appendElement("script") {}
//        }
//    }
//}