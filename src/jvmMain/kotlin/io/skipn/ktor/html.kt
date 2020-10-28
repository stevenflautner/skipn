package io.skipn.ktor

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.html.HTML
import kotlinx.html.consumers.delayed
import kotlinx.html.html
import kotlinx.html.stream.HTMLStreamBuilder

/**
 * Represents an [OutgoingContent] using `kotlinx.html` builder.
 */
class SkipnHtmlContent(
        override val status: HttpStatusCode? = null, private val route: String, private val builder: HTML.() -> Unit
) : OutgoingContent.WriteChannelContent() {

    override val contentType: ContentType
        get() = ContentType.Text.Html.withCharset(Charsets.UTF_8)

    override suspend fun writeTo(channel: ByteWriteChannel) {
        channel.bufferedWriter().use {
            it.append("<!DOCTYPE html>\n")

            HTMLStreamBuilder(it, prettyPrint = false, xhtmlCompatible = false).run {
                skipnContext.init(route)
                delayed()
            }.html(block = builder)
//            it.appendHTML().html(block = builder)
        }
    }
}

/**
 * Responds to a client with a HTML response, using specified [block] to build an HTML page
 */
suspend fun ApplicationCall.respondSkipnHtml(app: HTML.() -> Unit, status: HttpStatusCode = HttpStatusCode.OK) {
    val route = parameters["ROUTE"]?.let { "/$it" } ?: "/"
    respond(SkipnHtmlContent(status, route, app))
}