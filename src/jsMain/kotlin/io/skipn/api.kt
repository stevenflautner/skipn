import io.skipn.EndpointBase
import io.skipn.actions.getCookieString
import io.skipn.errors.ApiError
import io.skipn.platform.Cookies
import io.skipn.platform.DEV
import io.skipn.utils.decodeFromStringStatic
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import org.w3c.fetch.SAME_ORIGIN
import kotlin.js.json

object Api {
    val devUrl get() = "http://${window.location.hostname}:${8081}"

    suspend inline fun <reified RESP: Any> post(
        endpoint: EndpointBase<*, RESP>,
        body: Any,
        noinline headers: (kotlin.js.Json.() -> Unit)? = null
    ): RESP {
        val url = if (!DEV) {
            "${window.location.origin}${endpoint.route}"
        } else devUrl + endpoint.route

        println("POST: { URL: $url }")

        val credentials = if (!DEV) RequestCredentials.SAME_ORIGIN
        else RequestCredentials.INCLUDE

        val response = window.fetch(
            url,
            RequestInit(
                method = "post",
                credentials = credentials,
                headers = json().also { headersJson ->

                    // Required so that if we get a 'Set-Cookie' header in the
                    // response it is applied to the browser
//                    headersJson["Credentials"] = "same-origin"
//                    headersJson["Origin"] = "http://localhost:8080"

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

        if (DEV) {
            response.headers.get("Set-Cookie")?.let {
                Cookies.store(it)
            }
        }

        if (!response.ok) {
            throw ApiError(response.text().await())
        }

        val json = JSON.stringify(response.json().await())
        return Json.decodeFromStringStatic(json)
    }
}

//package io.skipn
//
//import io.ktor.client.*
//import io.ktor.client.call.*
//import io.ktor.client.features.*
//import io.ktor.client.features.json.*
//import io.ktor.client.features.json.serializer.*
//import io.ktor.client.request.*
//import io.ktor.client.statement.*
//import io.ktor.http.*
//import io.skipn.Skipn.apiJson
//import io.skipn.errors.ApiError
//import io.skipn.platform.DEV
//import io.skipn.utils.decodeFromStringStatic
//import kotlinx.browser.window
//import org.w3c.dom.HTMLFormElement
//import org.w3c.fetch.RequestInit
//import org.w3c.xhr.FormData
//
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
//
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
//
//// TODO Migrate this to using api instead of window.fetch
////  when post data can be converted from the dom or Form implementation
////  is updated so that it doesn't rely on the dom for getting input values
////inline fun <reified RESP: Any> postFormLegacyApi(
////    endpoint: FormEndpoint<*, RESP>,
////    form: HTMLFormElement,
////    crossinline onSuccess: (RESP) -> Unit
////) {
////    window.fetch(endpoint.route, init = RequestInit(
////        method = "post",
////        body = FormData(form),
//////        headers = json().apply {
//////            // Required so that if we get a 'Set-Cookie' header in the
//////            // response it is applied to the browser
//////            this["credentials"] = "same-origin"
//////        }
////    )).then { response ->
////        if (response.status == 200.toShort()) {
////            response.json()
////        } else {
////            println("there was an error...${response.status}")
////        }
////    }.then { json: dynamic ->
////        val jsonString = JSON.stringify(json)
////        val data = apiJson.decodeFromStringStatic<RESP>(jsonString)
////        onSuccess(data)
////    }
////}