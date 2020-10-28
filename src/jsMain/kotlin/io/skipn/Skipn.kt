package io.skipn

import io.skipn.utils.byId
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import io.skipn.builder.BuildContext
import io.skipn.html.body
import io.skipn.platform.DEV
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.dom.create
import kotlin.js.Date

//fun initSkipn(app: HTML.() -> Unit, onInitialized: () -> Unit) {
//    window.onload = {
//        val ct = Date().getMilliseconds()
//        Skipn.initialize(app)
//        println("Skipn initialized, took ${Date().getMilliseconds() - ct} ms")
//        onInitialized()
//    }
//}

object Skipn {

    val context = SkipnContext()

    fun initialize(app: HTML.() -> Unit) {
        val ct = Date().getMilliseconds()
        DEV = byId("skipn-main-script").getAttribute("data-dev") == "true"

        context.init(window.location.pathname)

        window.onpopstate = {
            context.route.setValue(window.location.pathname)
        }

        if (DEV) {
            val buildContext = BuildContext("skipn-app")
            document.documentElement!!.replaceWith(document.create(buildContext).html {
                app()
            })
        }
        else {
            context.resources.init(byId("skipn-res").getAttribute("data-res")!!)

            createHTML().html {
                app()
            }
        }

        context.isInitializing = false
        println("Skipn initialized, took ${Date().getMilliseconds() - ct} ms")
    }

}
