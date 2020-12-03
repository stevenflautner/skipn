package io.skipn.ktor

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.skipn.html.HtmlApp
import io.skipn.utils.buildApiJson
import java.util.concurrent.TimeUnit

fun Application.Skipn(
        app: HtmlApp.() -> Unit,
        endpoints: Routing.() -> Unit
) {
    install(ContentNegotiation) {
        json(buildApiJson())

        install(StatusPages) {
            exception<Exception> { cause ->
                cause.printStackTrace()
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
        anyHost()
    }
    install(Compression) {
        gzip()
    }

    routing {
        skipnRouting(app, endpoints)
    }
}