package io.skipn

import io.skipn.form.FormValidator

expect abstract class EndpointBase<REQ: Any, RESP: Any>(path: String) {
    val route: String
}
abstract class Endpoint<REQ: Any, RESP: Any>(path: String) : EndpointBase<REQ, RESP>(path)
abstract class FormEndpoint<REQ: Any, RESP: Any>(
    path: String,
    val validator: (FormValidator.() -> Unit)? = null
) : EndpointBase<REQ, RESP>(path)