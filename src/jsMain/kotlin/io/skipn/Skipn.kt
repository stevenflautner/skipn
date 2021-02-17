package io.skipn

import io.skipn.utils.byId
import kotlinx.html.*
import io.skipn.builder.BuildContext
import io.skipn.html.*
import io.skipn.platform.DEV
import io.skipn.utils.buildApiJson
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.js.onClickFunction
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import snabbdom.buildVDom

//fun initSkipn(app: HTML.() -> Unit, onInitialized: () -> Unit) {
//    window.onload = {
//        val ct = Date().getMilliseconds()
//        Skipn.initialize(app)
//        println("Skipn initialized, took ${Date().getMilliseconds() - ct} ms")
//        onInitialized()
//    }
//}

typealias ErrorSerializer<T> = suspend (String) -> Exception

object Snabbdom {
    val patch by lazy {
        SnabbdomInit.init(arrayOf(
            SnabbdomClassesModule.classModule,
            SnabbdomAttributesModule.attributesModule,
            SnabbdomStyleModule.styleModule,
            SnabbdomEventListenersModule.eventListenersModule
        ))
    }
}

object Skipn {

    val apiJson = buildApiJson()
    lateinit var context: SkipnContext
    private set

    lateinit var buildHash: String

    var runAfterInitialized: ArrayList<() -> Unit>? = arrayListOf()

    lateinit var errorSerializer: ErrorSerializer<Exception>

    fun initialize(app: HtmlApp.() -> Unit) {
        createSkipnContext { skipnContext ->
            context = skipnContext

            val mainScript = byId("skipn-main-script")
            DEV = mainScript.getAttribute("data-dev") == "true"
            buildHash = mainScript.getAttribute("data-hash")!!

            window.onpopstate = {
                val route = "${window.location.pathname}${window.location.search}"
                context.router.changeRoute(route)
            }

            val rootBuildContext = BuildContext.createRoot(skipnContext)

            if (DEV) {
                Snabbdom.patch(
                    SnabbdomToVNode.toVNode(document.documentElement!!),
                    buildVDom(rootBuildContext).html {
                        html(app)
                    }
                )
//                document.documentElement!!.replaceWith(document.create(rootBuildContext).html {
//                    html(app)
//                    body {
//                        div {
//                            onClickFunction
//                        }
//                    }
//                })
            }
            else {
                skipnContext.resources.init(byId("skipn-res").getAttribute("data-res")!!)

                Snabbdom.patch(
                    SnabbdomToVNode.toVNode(document.documentElement!!),
                    buildVDom(rootBuildContext).html {
                        html(app)
                    }
                )
//                buildVDom(rootBuildContext).html {
//                    html(app)
//                }

//                createHTML(rootBuildContext).html {
//                    html(app)
//                }
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
