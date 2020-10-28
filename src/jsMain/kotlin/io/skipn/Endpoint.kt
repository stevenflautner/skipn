package io.skipn

actual abstract class Endpoint<REQ: Any, RESP: Any> actual constructor(path: String) {
    val route = "/api$path"
}