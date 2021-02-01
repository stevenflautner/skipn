package io.skipn

import io.skipn.actions.getCookieString
import io.skipn.errors.ApiError
import io.skipn.platform.DEV
import io.skipn.utils.await
import io.skipn.utils.decodeFromStringStatic
import io.skipn.utils.encodeToStringStatic
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
import kotlin.js.json

object Api {
    suspend inline fun <reified RESP: Any> post(
        endpoint: EndpointBase<*, RESP>,
        body: Any,
        noinline headers: (kotlin.js.Json.() -> Unit)? = null
    ): RESP {
        val host = window.location.hostname
        val currentPort = window.location.port.toInt()
        val port = if (DEV) currentPort + 1 else currentPort

        val url = "http://$host:$port${endpoint.route}"

        val response = window.fetch(
            url,
            RequestInit(
                method = "post",
                headers = json().let { headersJson ->
                    // Required so that if we get a 'Set-Cookie' header in the
                    // response it is applied to the browser
                    headersJson["credentials"] = "same-origin"

                    // Send the required cookies to the server
                    endpoint.getCookieString()?.let {
                        headersJson["Cookie"] = it
                    }

                    headers?.let {
                        headersJson.apply(it)
                    }
                },
                body = body
            )
        ).await()

        val json = JSON.stringify(response.json().await())

        if (!response.ok) {
            throw ApiError(json)
        }

        return Json.decodeFromStringStatic(json)
    }
}

//val api = HttpClient {
//    install(JsonFeature) {
//        serializer = KotlinxSerializer()
//    }
//    HttpResponseValidator {
//        validateResponse { response: HttpResponse ->
//            if (response.status.value != 200)
//                throw ClientRequestException(response)
//
//        }
//
//        handleResponseException { cause: Throwable ->
////            throw Skipn.errorSerializer(cause.resp)
////            println("ERORORRRR?")
////            println(cause is ClientRequestException)
//
//
//            when(cause) {
//                is ClientRequestException -> {
////                    println(cause.response.content.readUTF8Line(5000)!!)
//
//                    throw ApiError(cause.response.content.readUTF8Line(5000)!!)
////                    throw Skipn.errorSerializer(cause.response.content.readUTF8Line(5000)!!)
//                }
////                is ClientRequestException -> println("CLIENT REQUEST EXCE")
////                else -> throw Exception("WOLLALA")
//            }
//        }
//    }
//    defaultRequest {
//        headers {
//            append("Origin", "http://localhost:8080")
//        }
////        contentType(ContentType.Application.Json)
//        host = window.location.hostname
//        val currentPort = window.location.port.toInt()
//        port = if (DEV) currentPort + 1 else currentPort
//    }
//}

//val multipartApi by lazy {
//    HttpClient {
////        install(JsonFeature) {
////            serializer = KotlinxSerializer()
////        }
//        defaultRequest {
////            contentType(ContentType.Application.Json)
//            host = window.location.hostname
//            port = window.location.port.toInt()
//        }
//    }
//}

// TODO Migrate this to using api instead of window.fetch
//  when post data can be converted from the dom or Form implementation
//  is updated so that it doesn't rely on the dom for getting input values
//inline fun <reified RESP: Any> postFormLegacyApi(
//    endpoint: FormEndpoint<*, RESP>,
//    form: HTMLFormElement,
//    crossinline onSuccess: (RESP) -> Unit
//) {
//    window.fetch(endpoint.route, init = RequestInit(
//        method = "post",
//        body = FormData(form),
////        headers = json().apply {
////            // Required so that if we get a 'Set-Cookie' header in the
////            // response it is applied to the browser
////            this["credentials"] = "same-origin"
////        }
//    )).then { response ->
//        if (response.status == 200.toShort()) {
//            response.json()
//        } else {
//            println("there was an error...${response.status}")
//        }
//    }.then { json: dynamic ->
//        val jsonString = JSON.stringify(json)
//        val data = apiJson.decodeFromStringStatic<RESP>(jsonString)
//        onSuccess(data)
//    }
//}