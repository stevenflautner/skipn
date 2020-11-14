package io.skipn.form

import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.skipn.api

actual suspend inline fun <reified RESP: Any> postForm(state: FormState): RESP {
    // Create snapshot of every input value
    return api.post(state.endpoint.route) {
        defaultSerializer()

        body = MultiPartFormDataContent(
            formData {

                // TODO SUPPORT UPLOAD OF FILES WITH APPROPRIATE HEADERS
                state.inputs.forEach {
                    append(it.key, it.value.valueAttr.value.toString())
                }

            }
        )
    }
}