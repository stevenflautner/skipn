package io.skipn.html

import io.skipn.Skipn
import io.skipn.skipnContext
import kotlinx.html.*

actual fun BODY.skipnBody() {
    unsafe {
        + """
            <script type="text/javascript" src="/public/${Skipn.buildHash}.js" id="skipn-main-script"></script>
        """.trimIndent()
    }
//    script {
//        type = "text/javascript"
//        src = "/public/${Skipn.buildHash}.js"
//        id = "skipn-main-script"
//    }
    script {
        attributes["id"] = "skipn-res"
        attributes["data-res"] = skipnContext.resources.createSnapshot()
    }
}

actual fun HEAD.skipnHead() {
    meta { charset = "utf-8" }
    meta { attributes["http-equiv"] = "Cache-control"; content = "public" }
    meta { name = "viewport"; content = "width=device-width, initial-scale=1" }
    link { rel = "stylesheet"; type="text/css"; href = "/public/${Skipn.buildHash}.css" }
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
actual inline fun FlowContent.script(
    type: String?,
    src: String?,
    crossinline block: SCRIPT.() -> Unit
) {
    // Empty function should not have a body
}