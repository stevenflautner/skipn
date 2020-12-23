package io.skipn

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.skipn.actions.routePage
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

    inline fun <reified T: Exception> errorSerializer() {
        errorSerializer = {
            println(it)
            val a =Json.decodeFromString<T>(it)
            println("ERRRRRRR")
            println(T::class)
            println(a)
            a
//            it.receive<T>()
        }
    }

    fun initialize(app: HtmlApp.() -> Unit) {
        createSkipnContext { skipnContext ->
            context = skipnContext

            DEV = byId("skipn-main-script").getAttribute("data-dev") == "true"

            window.onpopstate = {
                routePage(window.location.pathname)
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

internal fun ensureRunAfterInitialization(body: () -> Unit) {
    if (Skipn.context.isInitializing) {
        Skipn.runAfterInitialized!!.add(body)
        return
    }
    body()
}
