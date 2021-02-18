package io.skipn.ktor

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.skipn.ErrorFilter
import io.skipn.errors.ApiError
import io.skipn.html.HtmlApp
import io.skipn.platform.DEV
import io.skipn.utils.buildApiJson
import java.util.concurrent.TimeUnit

fun Application.Skipn(
        app: HtmlApp.() -> Unit,
        endpoints: Routing.() -> Unit,
        errorFilter: ErrorFilter? = null
) {
    install(ContentNegotiation) {
        json(buildApiJson())
//        json()

        install(StatusPages) {
            exception<Exception> { cause ->
                cause.printStackTrace()

//                errorFilter?.let {
//
//                    it(cause, call)
//
////                    if (it(cause)) {
////                        call.respond(HttpStatusCode.BadRequest, cause)
////                    }
//                }

                if (cause is ApiError) {
                    val msg = cause.msg

                    if (msg == null)
                        call.respond(HttpStatusCode.BadRequest)
                    else
                        call.respond(HttpStatusCode.BadRequest, msg)
                }
            }
        }
    }
    install(CachingHeaders) {
        val cacheAgeMax = TimeUnit.DAYS.toSeconds(365).toInt()
        val cacheMax = CachingOptions(
            cacheControl = CacheControl.MaxAge(
                maxAgeSeconds = cacheAgeMax,
            )
        )

        options { _outgoingContent ->
            val outgoingContent = _outgoingContent.contentType?.withoutParameters() ?: return@options null

            if (outgoingContent.match(ContentType.Image.Any)) {
                return@options CachingOptions(
                    cacheControl = CacheControl.MaxAge(
                        mustRevalidate = false,
                        maxAgeSeconds = 7 * 24 * 60 * 60,
                        visibility = CacheControl.Visibility.Public
                    )
                )
            }

            when (outgoingContent) {
                ContentType.Application.JavaScript -> cacheMax
                ContentType.Text.JavaScript -> cacheMax
                ContentType.Text.CSS -> cacheMax
                else -> null
            }
        }
    }
    install(CORS) {
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Delete)
        method(HttpMethod.Options)
//        host("localhost:8080")
        if (DEV) {
            host("localhost:8080")
        } else {
            anyHost()
        }
        allowCredentials = true
        header(HttpHeaders.AccessControlAllowOrigin)
        header(HttpHeaders.ContentType)
        exposeHeader(HttpHeaders.ContentType)
        exposeHeader(HttpHeaders.AccessControlAllowOrigin)
//        if (DEV) {
//        }
        header(HttpHeaders.AccessControlAllowCredentials)
        exposeHeader(HttpHeaders.AccessControlAllowCredentials)

    }
    install(Compression) {
        gzip()
    }

    routing {
        skipnRouting(app, endpoints)
    }
}