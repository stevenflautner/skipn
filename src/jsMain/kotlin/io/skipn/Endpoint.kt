package io.skipn

actual abstract class EndpointBase<REQ : Any, RESP : Any> actual constructor(path: String) {
    actual val route = "/api$path"
}