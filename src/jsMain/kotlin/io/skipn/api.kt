package io.skipn

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.skipn.Skipn.apiJson
import io.skipn.utils.decodeFromStringStatic
import kotlinx.browser.window
import org.w3c.dom.HTMLFormElement
import org.w3c.fetch.RequestInit
import org.w3c.xhr.FormData

val api = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    defaultRequest {
//        contentType(ContentType.Application.Json)
        host = window.location.hostname
        port = window.location.port.toInt()
    }
}

val multipartApi by lazy {
    HttpClient {
//        install(JsonFeature) {
//            serializer = KotlinxSerializer()
//        }
        defaultRequest {
//            contentType(ContentType.Application.Json)
            host = window.location.hostname
            port = window.location.port.toInt()
        }
    }
}

// TODO Migrate this to using api instead of window.fetch
//  when post data can be converted from the dom or Form implementation
//  is updated so that it doesn't rely on the dom for getting input values
inline fun <reified RESP: Any> postFormLegacyApi(
    endpoint: FormEndpoint<*, RESP>,
    form: HTMLFormElement,
    crossinline onSuccess: (RESP) -> Unit
) {
    window.fetch(endpoint.route, init = RequestInit(
        method = "post",
        body = FormData(form),
//        headers = json().apply {
//            // Required so that if we get a 'Set-Cookie' header in the
//            // response it is applied to the browser
//            this["credentials"] = "same-origin"
//        }
    )).then { response ->
        if (response.status == 200.toShort()) {
            response.json()
        } else {
            println("there was an error...${response.status}")
        }
    }.then { json: dynamic ->
        val jsonString = JSON.stringify(json)
        val data = apiJson.decodeFromStringStatic<RESP>(jsonString)
        onSuccess(data)
    }
}