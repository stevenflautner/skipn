package io.skipn

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.skipn.form.FormValidator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

actual abstract class EndpointBase<REQ : Any, RESP : Any> actual constructor(path: String) {
    actual val route = "/api$path"
    lateinit var implementedFunc: suspend ApplicationCall.(REQ) -> RESP
}

//actual abstract class Endpoint<REQ: Any, RESP: Any> actual constructor(path: String) {
//    lateinit var implementedFunc: suspend ApplicationCall.(REQ) -> RESP
//}

inline fun <reified REQ: Any, RESP: Any> Routing.endpoint(
    endpoint: Endpoint<REQ, RESP>,
    noinline func: suspend ApplicationCall.(REQ) -> RESP
) {
    endpoint.implementedFunc = func

    post(endpoint.route) {
//        val contentType = call.request.contentType()
//        val request = when {
//            contentType.match(ContentType.MultiPart.FormData) -> {
//                // Parse as multipart/form data instead of JSON
//                call.parseMultipart<REQ>()
//            }
//            contentType.match(ContentType.Application.Json) -> {
//                val json = call.receive<String>().removeSurrounding("\"")
//                Json.decodeFromString(json)
//            }
//            else -> throw Exception("Content type could not be handled by endpoint at ${endpoint.route}")
//        }
        val json = call.receive<String>().removeSurrounding("\"")
        val request = Json.decodeFromString<REQ>(json)

        val response = call.func(request)
        call.respond(response)
    }
    println("Endpoint serving at ${endpoint.route}")
}

inline fun <reified REQ: Any, RESP: Any> Routing.formEndpoint(
    endpoint: FormEndpoint<REQ, RESP>,
    noinline func: suspend ApplicationCall.(REQ) -> RESP
) {
    endpoint.implementedFunc = func

    post(endpoint.route) {
        // Parse as multipart/form data instead of JSON
        val requestMap = call.parseMultipart<REQ>()

        val validator = endpoint.validator
        if (validator != null) {
            // Perform custom logic against form snapshot
            val customValidator = FormValidator(requestMap).apply {
                validator()
            }
            println("THIS IS THE VALUES")
            println(requestMap)
            if (customValidator.validationError != null) {
                throw Exception("""
Form validation failed on the server, this should not happen if you used Skipn's form
mechanic to post the form. If you did, it's a bug. This went wrong: ${customValidator.validationError}
                """.trimIndent())
            }
        }

        val request = convertMapToInstance<REQ>(requestMap)

//        val request = when {
//            contentType.match(ContentType.MultiPart.FormData) -> {
//
//            }
//            contentType.match(ContentType.Application.Json) -> {
//                val json = call.receive<String>().removeSurrounding("\"")
//                Json.decodeFromString(json)
//            }
//            else -> throw Exception("Content type could not be handled by endpoint at ${endpoint.route}")
//        }
        val response = call.func(request)
        call.respond(response)
    }
    println("Endpoint serving at ${endpoint.route}")
}

//inline fun <reified REQ: Any, RESP: Any> Routing.formEndpoint(endpoint: Endpoint<REQ, RESP>, noinline func: suspend (REQ) -> RESP) {
//    endpoint.implementedFunc = func
//
//    post(endpoint.route) {
//        // Parse as multipart/form data instead of JSON
//        val data = call.parseMultipart<REQ>()
//        call.respond(func(data))
//    }
//    println("Endpoint serving at ${endpoint.route}")
//}