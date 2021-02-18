@file:JvmMultifileClass
@file:JvmName("Endpoint_")

package io.skipn

import io.skipn.form.FormValidator
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

//fun String.toDashCase() = replace(humps, "-").toLowerCase()
//private val humps = "(?<=.)(?=\\p{Upper})".toRegex()

expect abstract class EndpointBase<REQ: Any, RESP: Any>(cookies: List<String>?) {
    val cookies: List<String>?
}
abstract class Endpoint<REQ: Any, RESP: Any>(cookies: List<String>? = null) : EndpointBase<REQ, RESP>(cookies)

abstract class FormEndpoint<REQ: Any, RESP: Any>(
    val validator: (FormValidator.() -> Unit)? = null,
    cookies: List<String>? = null
) : EndpointBase<REQ, RESP>(cookies)

internal fun parseApiRoute(className: Any) = "/api/${className::class.simpleName}"