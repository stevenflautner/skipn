package io.skipn

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.browser.window

val api = HttpClient {
    defaultRequest { // this: HttpRequestBuilder ->
        host = window.location.hostname
        port = window.location.port.toInt()
//                header("X-My-Header", "MyValue")
    }
//            install(JsonFeature) {
//                serializer = KotlinxSerializer()
//            }
}