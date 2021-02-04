package io.skipn.utils

import io.skipn.state.*

fun <T: Any?> result() = stateOf<Result<T>?>(null)

fun <T: Any> State<Result<T>?>.onSuccess() = filter { it != null && it.isSuccess }