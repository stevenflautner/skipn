package io.skipn.actions

import Api
import io.skipn.Endpoint
import io.skipn.EndpointBase
import io.skipn.SkipnContext
import io.skipn.errors.ApiError
import io.skipn.platform.Cookies
import io.skipn.platform.DEV
import io.skipn.utils.encodeToStringStatic
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json

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
        job = GlobalScope.launch {
            try {
                val load = load()
                onSuccess(load)
            } catch (e: ApiError) {
                onFailure(e)
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
    Api.post(
        endpoint,
        body = Json.encodeToStringStatic(request),
        headers = {
            set("Content-Type", "application/json")
        },
    )
}

@OptIn(InternalSerializationApi::class)
actual inline fun <reified REQ : Any, reified RESP : Any> browserPost(
    endpoint: Endpoint<REQ, RESP>,
    request: REQ
): suspend () -> RESP = {
    Api.post(
        endpoint,
        body = Json.encodeToStringStatic(request),
        headers = {
            set("Content-Type", "application/json")
        },
    )
}

fun EndpointBase<*, *>.getCookieString(): String? {
    cookies?.let { cookieNames ->
        val browserCookies = Cookies.getAll()
        val cookieString = StringBuilder()

        cookieNames.forEach { cookieName ->
            browserCookies[cookieName]?.let {
                cookieString.append(" $it;")
            }
        }
        return cookieString.toString()
    }
    return null
}