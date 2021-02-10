package io.skipn.utils

import io.skipn.errors.ApiError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.jvm.JvmField

sealed class ApiResult<out T : Any> {
    object Loading : ApiResult<Nothing>()
    class Value<out T : Any>(@JvmField val value: T) : ApiResult<T>()
    class Error(@JvmField val error: ApiError) : ApiResult<Nothing>()
}

fun <T: Any> apiResult(): MutableStateFlow<ApiResult<T>?> = mutableStateFlowOf(null)