package io.skipn.actions

import io.ktor.client.request.*
import io.ktor.http.*
import io.skipn.Endpoint
import io.skipn.platform.DEV
import io.skipn.SkipnContext
import io.skipn.api
import io.skipn.utils.decodeFromStringStatic
import io.skipn.utils.encodeToStringStatic
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

actual inline fun <reified RESP : Any> loader(
    skipnContext: SkipnContext,
    noinline load: suspend () -> RESP,
    noinline onSuccess: ((RESP) -> Unit)?
) : LoadTask<RESP> {
    val task = LoadTask(load, onSuccess)

    if (skipnContext.isInitializing) {

        if (DEV) {
            task.execute()
        }
        else {
            // No need to execute task just preload response
            // that was provided as a resource from the Skipn Server
            skipnContext.resources.get<RESP>().let {
                task.response = it
                onSuccess?.invoke(it)
            }
        }
    } else {
        task.execute()
    }
    return task
}

actual class LoadTask<RESP : Any> actual constructor(
    private val load: suspend () -> RESP,
    private val onSuccess: ((RESP) -> Unit)?
) {

    private var job: Job? = null
    actual var response: RESP? = null

    actual fun execute() {
        GlobalScope.apply {
            job = launch {
                load().let {
                    response = it
                    onSuccess?.invoke(it)
                }
            }
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