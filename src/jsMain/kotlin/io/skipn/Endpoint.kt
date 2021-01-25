package io.skipn

actual abstract class EndpointBase<REQ : Any, RESP : Any> {
    val route = parseApiRoute(this)
}