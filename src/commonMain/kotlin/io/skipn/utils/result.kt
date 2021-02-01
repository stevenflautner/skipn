package io.skipn.utils

import io.skipn.state.State
import io.skipn.state.filter
import io.skipn.state.filterNotNull
import io.skipn.state.stateOf

fun <T: Any?> result() = stateOf<Result<T>?>(null)

fun <T: Any> State<Result<T>?>.onSuccess() = filterNotNull().filter { it.isSuccess }