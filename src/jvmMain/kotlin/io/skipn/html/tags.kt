package io.skipn.html

import io.skipn.Skipn
import io.skipn.skipnContext
import kotlinx.html.*

actual fun BODY.skipnBody() {
//    val snapshot = skipnContext.resources.createSnapshot()
//    unsafe {
//        + """
//            <script type="text/javascript" id="skipn-res" data-res="$snapshot"></script>
//        """.trimIndent()
//    }
//    unsafe {
//        + """
////            <script type="text/javascript" src="/public/${Skipn.buildHash}.js" id="skipn-main-script"></script>
//            <script type="text/javascript" id="skipn-res" data-res="${snapshot.replace("\"", "'")}"></script>
//        """.trimIndent()
//    }
    scriptInternal {
        attributes["id"] = "skipn-res"
        attributes["data-res"] = skipnContext.resources.createSnapshot()
    }
}

actual fun HEAD.skipnHead() {
    meta { charset = "utf-8" }
    meta { attributes["http-equiv"] = "Cache-control"; content = "public" }
    meta { name = "viewport"; content = "width=device-width, initial-scale=1" }
    link {
        rel = "stylesheet"; type="text/css"; href = "/public/${Skipn.buildHash}.css"
    }

    scriptInternal {
        type = "text/javascript"
        src = "/public/${Skipn.buildHash}.js"
        attributes["id"] = "skipn-main-script"
        attributes["data-hash"] = Skipn.buildHash
    }
}

/**
 * Scripts are not included in the server prerender.
 * If you add a script to the Head, it will be appended
 * to the DOM's head once the framework has initialized,
 * therefore it will load afterwards.
 *
 * Server ignores the script tag defined
 */
@HtmlTagMarker
actual inline fun FlowOrMetaDataOrPhrasingContent.script(
    type: String?,
    src: String?,
    crossinline block: SCRIPT.() -> Unit
) {
    // Empty function should not have a body
}