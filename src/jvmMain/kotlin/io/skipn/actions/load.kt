package io.skipn.actions

import io.skipn.Endpoint
import io.skipn.SkipnContext
import io.skipn.errors.BrowserOnlyFunction
import kotlinx.coroutines.runBlocking

actual class LoadTask<RESP : Any> actual constructor(
    private val load: suspend () -> RESP,
) {

    actual fun execute(onSuccess: ResponseSuccess<RESP>, onFailure: ResponseFailure) {
        runBlocking {
            onSuccess.invoke(load())
        }
    }

    actual fun cancel() {
        throw BrowserOnlyFunction
    }
}

actual inline fun <reified RESP : Any> loader(
    skipnContext: SkipnContext,
    noinline load: suspend () -> RESP,
    response: Response<RESP>
) {
    LoadTask(load).execute(
        onSuccess = { loadResponse ->
            skipnContext.resources.add(loadResponse)
            response.success = loadResponse
        },
        onFailure = {
            response.apiError = it
        }
    )
}

actual inline fun <reified REQ : Any, reified RESP : Any> endpointFunc(
    skipnContext: SkipnContext,
    endpoint: Endpoint<REQ, RESP>,
    request: REQ
): suspend () -> RESP = {
    endpoint.implementedFunc(skipnContext.applicationCall, request)
}

actual inline fun <reified REQ : Any, reified RESP : Any> browserPost(
    endpoint: Endpoint<REQ, RESP>,
    request: REQ
): suspend () -> RESP {
    throw BrowserOnlyFunction
}