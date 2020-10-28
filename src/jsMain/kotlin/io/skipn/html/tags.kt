package io.skipn.html

import io.skipn.platform.DEV
import kotlinx.html.*

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