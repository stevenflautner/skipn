package io.skipn

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.getContextualOrDefault

actual abstract class Endpoint<REQ: Any, RESP: Any> actual constructor(path: String) {
    val route = "/api$path"
    lateinit var implementedFunc: (REQ) -> RESP
}

inline fun <reified REQ: Any, RESP: Any> Routing.endpoint(endpoint: Endpoint<REQ, RESP>, noinline func: (REQ) -> RESP) {
    endpoint.implementedFunc = func

    post(endpoint.route) {
        if (call.request.contentType().match(ContentType.MultiPart.FormData)) {
            // Parse as multipart/form data instead of JSON
            val data = call.parseMultipart<REQ>()
            call.respond(func(data))
        }
        else if (call.request.contentType().match(ContentType.Application.Json)) {
            val json = call.receive<String>().removeSurrounding("\"")
            val request = Json.decodeFromString<REQ>(Json.serializersModule.getContextualOrDefault(), json)
            call.respond(func(request))
        }
    }
    println("Endpoint serving at ${endpoint.route}")
}

inline fun <reified REQ: Any, RESP: Any> Routing.formEndpoint(endpoint: Endpoint<REQ, RESP>, noinline func: (REQ) -> RESP) {
    endpoint.implementedFunc = func

    post(endpoint.route) {
        // Parse as multipart/form data instead of JSON
        val data = call.parseMultipart<REQ>()
        call.respond(func(data))
    }
    println("Endpoint serving at ${endpoint.route}")
}