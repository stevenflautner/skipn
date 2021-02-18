@file:JvmMultifileClass
@file:JvmName("load_")

package io.skipn.actions

import io.skipn.Endpoint
import io.skipn.SkipnContext
import io.skipn.builder.BuildContext
import io.skipn.errors.ApiError
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

typealias ResponseSuccess <RESP> = (RESP) -> Unit
typealias ResponseFailure = (ApiError) -> Unit

//@OptIn(ExperimentalContracts::class)
//inline fun <RESP: Any> Response<RESP>.fold(
//    onSuccess: ResponseSuccess<RESP>,
//    onFailure: ResponseFailure
//) {
//    contract {
//        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
//        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
//    }
//    return when (val success = success) {
//        null -> onFailure(apiError ?: ApiError("Internal Api error"))
//        else -> onSuccess(success)
//    }
//}

class Response<RESP: Any> {
    var apiError: ApiError? = null
    var success: RESP? = null

    var task: LoadTask<RESP>? = null

    internal var onSuccess: ((RESP) -> Unit)? = null
    internal var onFailure: ((ApiError) -> Unit)? = null

    fun success(resp: RESP) {
        this.success = resp
        onSuccess?.invoke(resp)
    }

    fun cancel() {
        task?.cancel()
    }

    fun fail(error: ApiError) {
        apiError = error
        onFailure?.invoke(error)
    }
}

fun <RESP: Any> Response<RESP>.onSuccess(onSuccess: ResponseSuccess<RESP>): Response<RESP> {
    this.onSuccess = onSuccess
    success?.let {
        onSuccess(it)
    }
    return this
}

fun <RESP: Any> Response<RESP>.onFailure(onFailure: ResponseFailure): Response<RESP> {
    this.onFailure = onFailure
    apiError?.let {
        onFailure(it)
    }
    return this
}

expect inline fun <reified RESP: Any> loader(
    skipnContext: SkipnContext,
    noinline load: suspend () -> RESP,
    response: Response<RESP>
)

expect inline fun <reified REQ : Any, reified RESP : Any> endpointFunc(
    skipnContext: SkipnContext,
    endpoint: Endpoint<REQ, RESP>,
    request: REQ
) : suspend () -> RESP

expect inline fun <reified REQ : Any, reified RESP : Any> browserPost(
    endpoint: Endpoint<REQ, RESP>,
    request: REQ
) : suspend () -> RESP

expect class LoadTask<RESP : Any>(
    load: suspend () -> RESP,
) {
    fun execute(onSuccess: ResponseSuccess<RESP>, onFailure: ResponseFailure)
    fun cancel()
}

inline fun <reified REQ: Any, reified RESP: Any> BuildContext.load(
        endpoint: Endpoint<REQ, RESP>,
        request: REQ): Response<RESP> {

    return Response<RESP>().also {
        loader(skipnContext, endpointFunc(skipnContext, endpoint, request), it)
    }
}

inline fun <reified REQ: Any, reified RESP: Any> Endpoint<REQ, RESP>.request(request: REQ, context: BuildContext): Response<RESP> {
    return context.load(this, request)
}

suspend inline fun <reified REQ: Any, reified RESP: Any> Endpoint<REQ, RESP>.requestSuspend(request: REQ): RESP {
    return browserPost(this, request).invoke()
}

//class LoadBuilder<REQ: Any, RESP: Any>(private val endpoint: Endpoint<REQ, RESP>) {
//    var onSuccess: ((RESP) -> Unit)? = null
//}
//
//fun <RESP: Any> LoadBuilder<*, RESP>.onSuccess(onSuccess: (RESP) -> Unit) {
//    this.onSuccess = onSuccess
//}
