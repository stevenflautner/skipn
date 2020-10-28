@file:JvmMultifileClass
@file:JvmName("load_")

package io.skipn.actions

import io.skipn.Endpoint
import io.skipn.SkipnContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

expect inline fun <reified RESP : Any> loader(
        skipnContext: SkipnContext,
        noinline load: suspend () -> RESP,
        noinline onSuccess: ((RESP) -> Unit)?
) : LoadTask<RESP>

expect inline fun <reified REQ : Any, reified RESP : Any> endpointFunc(
    endpoint: Endpoint<REQ, RESP>,
    request: REQ
) : suspend () -> RESP

expect class LoadTask<RESP : Any>(load: suspend () -> RESP, onSuccess: ((RESP) -> Unit)?) {

    var response: RESP?

    fun execute()
    fun cancel()
}

//inline fun <reified REQ: Any, reified RESP: Any> SkipnContext.load(
//        endpoint: Endpoint<REQ, RESP>,
//        request: REQ,
//        noinline onLoaded: (RESP) -> Unit)
//        = loader(
//            this, endpointFunc(endpoint, request), onLoaded
//        )

inline fun <reified REQ: Any, reified RESP: Any> SkipnContext.load(
        endpoint: Endpoint<REQ, RESP>,
        request: REQ,
        body: LoadBuilder<REQ, RESP>.() -> Unit): LoadTask<RESP> {

    val builder = LoadBuilder(endpoint).apply(body)
    return loader(this, endpointFunc(endpoint, request), builder.onSuccess)
}

class LoadBuilder<REQ: Any, RESP: Any>(private val endpoint: Endpoint<REQ, RESP>) {
    var onSuccess: ((RESP) -> Unit)? = null
}
//inline fun <reified REQ: Any> LoadBuilder<REQ, *>.send(request: () -> REQ) {
//    this.request = request()
//}
fun <RESP: Any> LoadBuilder<*, RESP>.onSuccess(onSuccess: (RESP) -> Unit) {
    this.onSuccess = onSuccess
}
