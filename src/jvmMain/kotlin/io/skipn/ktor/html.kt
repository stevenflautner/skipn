package io.skipn.ktor

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.skipn.SkipnContext
import io.skipn.builder.BuildContext
import io.skipn.provide.PinningContext
import kotlinx.coroutines.coroutineScope
import kotlinx.html.HTML
import kotlinx.html.consumers.delayed
import kotlinx.html.html
import kotlinx.html.stream.HTMLStreamBuilder

/**
 * Represents an [OutgoingContent] using `kotlinx.html` builder.
 */
class SkipnHtmlContent(
    private val skipnContext: SkipnContext,
    private val builder: HTML.() -> Unit
) : OutgoingContent.WriteChannelContent() {

    override val status = HttpStatusCode.OK
    override val contentType: ContentType
        get() = ContentType.Text.Html.withCharset(Charsets.UTF_8)

    override suspend fun writeTo(channel: ByteWriteChannel) {
        val rootBuildContext = BuildContext("skipn-root", PinningContext(parent = null))

        coroutineScope {
            rootBuildContext.coroutineScope = this

            channel.bufferedWriter().use {
                it.append("<!DOCTYPE html>\n")

                HTMLStreamBuilder(
                    it,
                    skipnContext,
                    rootBuildContext,
                    prettyPrint = false,
                    xhtmlCompatible = false,
                ).run {
                    delayed()
                }.html(block = builder)
            }
        }
    }
}

/**
 * Responds to a client with a HTML response, using specified [block] to build an HTML page
 */
suspend fun ApplicationCall.respondSkipnHtml(app: HTML.() -> Unit) {
    respond(SkipnHtmlContent(SkipnContext(parseRoute(), this), app))
}

fun ApplicationCall.parseRoute(): String {
    return parameters["ROUTE"]?.let { "/$it" } ?: "/"
}