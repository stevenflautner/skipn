package io.skipn.actions

import io.ktor.client.request.*
import io.ktor.http.*
import io.skipn.Endpoint
import io.skipn.platform.DEV
import io.skipn.SkipnContext
import io.skipn.api
import io.skipn.errors.ApiError
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

actual inline fun <reified RESP : Any> loader(
    skipnContext: SkipnContext,
    noinline load: suspend () -> RESP,
    response: Response<RESP>,
) {
    if (skipnContext.isInitializing && !DEV) {
        // No need to execute task just preload response
        // that was provided as a resource from the Skipn Server
        response.success = skipnContext.resources.get<RESP>()
    } else {
        response.task = LoadTask(load).apply {
            execute(
                onSuccess = {
                    response.success(it)
                },
                onFailure = {
                    response.fail(it)
                }
            )
        }
    }
}

actual class LoadTask<RESP : Any> actual constructor(
    private val load: suspend () -> RESP,
) {

    private var job: Job? = null

    actual fun execute(onSuccess: ResponseSuccess<RESP>, onFailure: ResponseFailure) {
        try {
            job = GlobalScope.launch {
                onSuccess(load())
            }
        } catch (e: ApiError) {
            onFailure(e)
        }
    }

    actual fun cancel() {
        job?.cancel()
    }
}

@OptIn(InternalSerializationApi::class)
actual inline fun <reified REQ : Any, reified RESP : Any> endpointFunc(
        skipnContext: SkipnContext,
        endpoint: Endpoint<REQ, RESP>,
        request: REQ
): suspend () -> RESP = {
    api.post(endpoint.route) {
        contentType(ContentType.Application.Json)

        body = request
    }
}