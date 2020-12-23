package io.skipn.ktor

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.skipn.html.HtmlApp

fun Routing.skipnRouting(app: HtmlApp.() -> Unit, endpoints: Routing.() -> Unit) {
    endpoints()

    suspend fun PipelineContext<Unit, ApplicationCall>.serveApp() {
        val start = System.currentTimeMillis()
        kotlin.runCatching {
            call.respondSkipnHtml(app)
        }.onFailure {
            it.printStackTrace()
        }
        println("Measured: ${System.currentTimeMillis() - start}ms")
    }

    static("public") {
        resources("public")
    }

    get("{...}") {
        serveApp()
    }
//    get("/{ROUTE}") {
//        serveApp()
//    }
//    get("/{ROUTE}/{R1}") {
//        println("THIS CALLEDD??")
//        serveApp()
//    }
//    get("/{ROUTE}") {
//        serveApp()
//    }

//    get("/favicon.ico") {
//        println("SHOULD RETURN FAVICON")
//    }


}