package io.skipn

import io.skipn.utils.byId
import kotlinx.html.*
import io.skipn.builder.BuildContext
import io.skipn.html.*
import io.skipn.platform.DEV
import io.skipn.utils.buildApiJson
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

//fun initSkipn(app: HTML.() -> Unit, onInitialized: () -> Unit) {
//    window.onload = {
//        val ct = Date().getMilliseconds()
//        Skipn.initialize(app)
//        println("Skipn initialized, took ${Date().getMilliseconds() - ct} ms")
//        onInitialized()
//    }
//}

typealias ErrorSerializer<T> = suspend (String) -> Exception

object Skipn {

    val apiJson = buildApiJson()
    lateinit var context: SkipnContext
    private set

    var runAfterInitialized: ArrayList<() -> Unit>? = arrayListOf()

    lateinit var errorSerializer: ErrorSerializer<Exception>

    fun initialize(app: HtmlApp.() -> Unit) {
        createSkipnContext { skipnContext ->
            context = skipnContext

            DEV = byId("skipn-main-script").getAttribute("data-dev") == "true"

            window.onpopstate = {
                val route = "${window.location.pathname}${window.location.search}"
                context.router.changeRoute(route)
            }

            val rootBuildContext = BuildContext.createRoot(skipnContext)

            if (DEV) {
                document.documentElement!!.replaceWith(document.create(rootBuildContext).html {
                    html(app)
                })
            }
            else {
                skipnContext.resources.init(byId("skipn-res").getAttribute("data-res")!!)

                createHTML(rootBuildContext).html {
                    html(app)
                }
            }
        }
        runAfterInitialized!!.forEach {
            it()
        }
        runAfterInitialized = null
    }
}

fun ensureRunAfterInitialization(body: () -> Unit) {
    if (Skipn.context.isInitializing) {
        Skipn.runAfterInitialized!!.add(body)
        return
    }
    body()
}
