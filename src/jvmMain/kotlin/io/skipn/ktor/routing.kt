package io.skipn.ktor

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.html.HTML

fun Routing.skipnRouting(app: HTML.() -> Unit, endpoints: Routing.() -> Unit) {
    endpoints()

    suspend fun PipelineContext<Unit, ApplicationCall>.serveApp() {
        val start = System.currentTimeMillis()
        call.respondSkipnHtml(app)
        println("Measured: ${System.currentTimeMillis() - start}ms")
    }

    get("/") {
        serveApp()
    }
    get("/{ROUTE}") {
        serveApp()
    }

//    get("/favicon.ico") {
//        println("SHOULD RETURN FAVICON")
//    }

    static("public") {
        resources("public")
    }
}