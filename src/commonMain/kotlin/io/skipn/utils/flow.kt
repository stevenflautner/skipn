package io.skipn.utils

import io.skipn.builder.BuildContext
import kotlinx.coroutines.flow.*

fun <T: Any?> mutableStateFlowOf(value: T) = MutableStateFlow(value)

fun <T: Any?> result() = mutableStateFlowOf<Result<T>?>(null)

inline fun <T: Any?> BuildContext.collect(flow: Flow<T>, crossinline action: suspend (value: T) -> Unit) {
    launch {
        flow.collect {
            action(it)
        }
    }
}

fun <T: Any> StateFlow<Result<T>?>.onSuccess() = filterNotNull().filter { it.isSuccess }
//inline fun <T: Any> StateFlow<Result<T>?>.onSuccess() {
//    context.launch {
//        filterNotNull().collect { result ->
//            result.getOrNull()?.let { value ->
//                action(value)
//            }
//        }
//    }
//}