@file:JvmMultifileClass
@file:JvmName("Endpoint_")

package io.skipn

import io.skipn.form.FormValidator
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

//fun String.toDashCase() = replace(humps, "-").toLowerCase()
//private val humps = "(?<=.)(?=\\p{Upper})".toRegex()

expect abstract class EndpointBase<REQ: Any, RESP: Any>()
abstract class Endpoint<REQ: Any, RESP: Any> : EndpointBase<REQ, RESP>()

abstract class FormEndpoint<REQ: Any, RESP: Any>(
    val validator: (FormValidator.() -> Unit)? = null
) : EndpointBase<REQ, RESP>()

internal fun parseApiRoute(className: Any) = "/api/${(className::class.simpleName)}"