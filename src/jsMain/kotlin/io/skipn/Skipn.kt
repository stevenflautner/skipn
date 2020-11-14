package io.skipn

import io.skipn.actions.routePage
import io.skipn.utils.byId
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import io.skipn.builder.BuildContext
import io.skipn.html.body
import io.skipn.platform.DEV
import io.skipn.provide.PinningContext
import io.skipn.utils.buildApiJson
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.dom.create
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
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

    val context = SkipnContext(window.location.pathname)
    val apiJson = buildApiJson()

    fun initialize(app: HTML.() -> Unit) {
        val ct = Date().getMilliseconds()
        DEV = byId("skipn-main-script").getAttribute("data-dev") == "true"

        window.onpopstate = {
            routePage(window.location.pathname)
            Unit
        }

        val rootBuildContext = BuildContext("skipn-root", PinningContext(parent = null))
        rootBuildContext.coroutineScope = MainScope()

        if (DEV) {
            document.documentElement!!.replaceWith(document.create(rootBuildContext).html {
                app()
            })
        }
        else {
            context.resources.init(byId("skipn-res").getAttribute("data-res")!!)

            createHTML(context, rootBuildContext).html {
                app()
            }
        }

        context.isInitializing = false
        println("Skipn initialized, took ${Date().getMilliseconds() - ct} ms")
    }
}
