package io.skipn.errors

import kotlinx.serialization.Serializable

//@Serializable
//data classApiException (val error: ApiError) : Exception()

@Serializable
data class ApiError(val msg: String? = null): Exception()