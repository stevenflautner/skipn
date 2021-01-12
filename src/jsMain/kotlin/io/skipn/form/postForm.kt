package io.skipn.form

import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.skipn.api
import io.skipn.errors.ApiError

actual suspend inline fun <reified RESP: Any> postForm(state: FormState<RESP>): RESP {
    // Create snapshot of every input value
    return try {
        api.post(state.endpoint.route) {
            defaultSerializer()

            body = MultiPartFormDataContent(
                formData {

                    // TODO SUPPORT UPLOAD OF FILES WITH APPROPRIATE HEADERS
                    state.inputs.forEach { entry ->
                        when (val field = entry.value) {
                            is InputField -> {
                                field.valueAttr.value?.let {
                                    append(entry.key, it.toString())
                                }
                            }
                            is ValueField -> {
                                append(entry.key, field.toString())
//                               append(it.key, field.toString())
                            }
                        }
                    }

                }
            )
        }
    } catch (e: Exception) {
        if (e is ApiError)
            state.error(e.msg ?: "Szerver hiba történt, kérlek próbáld újra")
        else
            state.error("Hiba történt, kérlek próbáld újra")
        throw e
    }

//    return api.post(state.endpoint.route) {
//        defaultSerializer()
//
//        body = MultiPartFormDataContent(
//            formData {
//
//                // TODO SUPPORT UPLOAD OF FILES WITH APPROPRIATE HEADERS
//                state.inputs.forEach {
//                    val field = it.value
//
//                    when (field) {
//                        is InputField -> append(it.key, field.valueAttr.value.toString())
//                        is ValueField -> append(it.key, field.toString())
//                    }
//                }
//
//            }
//        )
//    }
}