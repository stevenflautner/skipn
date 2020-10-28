package io.skipn.actions

import io.ktor.client.request.*
import io.ktor.http.*
import io.skipn.Endpoint
import io.skipn.platform.DEV
import io.skipn.SkipnContext
import io.skipn.api
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.getContextualOrDefault

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

actual inline fun <reified REQ : Any, reified RESP : Any> endpointFunc(
        endpoint: Endpoint<REQ, RESP>,
        request: REQ
): suspend () -> RESP = {
    val post = api.post<String>(endpoint.route) {
        contentType(ContentType.Application.Json)

        body = Json.encodeToString(request)
    }

    // TODO Update once overload resolution ambiguity is resolved
    Json.decodeFromString(Json.serializersModule.getContextualOrDefault(), post)
}