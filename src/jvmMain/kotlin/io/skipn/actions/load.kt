package io.skipn.actions

import io.skipn.Endpoint
import io.skipn.SkipnContext
import kotlinx.coroutines.runBlocking
import kotlin.reflect.full.primaryConstructor

actual class LoadTask<RESP : Any> actual constructor(
    private val load: suspend () -> RESP,
    private val onSuccess: ((RESP) -> Unit)?
) {

    actual var response: RESP? = null

    actual fun execute() {
        runBlocking {
            load().let {
                response = it
                onSuccess?.invoke(it)
            }
        }
    }

    actual fun cancel() {
    }
}

actual inline fun <reified RESP : Any> loader(
    skipnContext: SkipnContext,
    noinline load: suspend () -> RESP,
    noinline onSuccess: ((RESP) -> Unit)?
): LoadTask<RESP> {
    val task = LoadTask(load, { response ->
        skipnContext.resources.add(response)
        onSuccess?.invoke(response)
    })
    task.execute()
    return task
}

actual inline fun <reified REQ : Any, reified RESP : Any> endpointFunc(
    skipnContext: SkipnContext,
    endpoint: Endpoint<REQ, RESP>,
    request: REQ
): suspend () -> RESP = {
    endpoint.implementedFunc(skipnContext.applicationCall, request)
}