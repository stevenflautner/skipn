package io.skipn.ktor

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.skipn.html.HTMLStreamBuilder
import io.skipn.SkipnContext
import io.skipn.builder.BuildContext
import io.skipn.html.HtmlApp
import io.skipn.html.html
import kotlinx.html.consumers.delayed
import kotlinx.html.html

/**
 * Represents an [OutgoingContent] using `kotlinx.html` builder.
 */
class SkipnHtmlContent(
    private val skipnContext: SkipnContext,
    private val builder: HtmlApp.() -> Unit
) : OutgoingContent.WriteChannelContent() {

    override val status = HttpStatusCode.OK
    override val contentType: ContentType
        get() = ContentType.Text.Html.withCharset(Charsets.UTF_8)

    override suspend fun writeTo(channel: ByteWriteChannel) {
        val rootBuildContext = BuildContext.createRoot(skipnContext)

        channel.bufferedWriter().use {
            it.append("<!DOCTYPE html>\n")

            HTMLStreamBuilder(
                    it,
                    rootBuildContext,
                    prettyPrint = false,
                    xhtmlCompatible = false,
            ).run {
                delayed()
            }.html {
                html(builder)
            }
        }
//
//        coroutineScope {
//            rootBuildContext.coroutineScope = this
//
//            channel.bufferedWriter().use {
//                it.append("<!DOCTYPE html>\n")
//
//                HTMLStreamBuilder(
//                    it,
//                    rootBuildContext,
//                    prettyPrint = false,
//                    xhtmlCompatible = false,
//                ).run {
//                    delayed()
//                }.html(block = builder)
//            }
//        }
    }
}

/**
 * Responds to a client with a HTML response, using specified [block] to build an HTML page
 */
suspend fun ApplicationCall.respondSkipnHtml(app: HtmlApp.() -> Unit) {
    respond(SkipnHtmlContent(SkipnContext(request.uri, this), app))
}

//fun ApplicationCall.parseRoute(): String {
//    println("URI")
//    println(request.uri)
//    return request.uri.removeSuffix("/")
////    val url = parameters.getAll("ROUTE")?.fold("") { acc, v -> "$acc/$v" }
////
////    return parameters["ROUTE"]?.let { "/$it" } ?: "/"
//}