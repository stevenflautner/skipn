package io.skipn.observers

import kotlinx.coroutines.flow.StateFlow

fun <T: Any?, R: Any?> StateFlow<T>.mapState(transform: (T) -> R): StateFlowDef<T, R> {
    return StateFlowDef(this, transform)
}

fun <T: Any?, R: Any?, RR: Any?> StateFlowDef<T, R>.mapState(transform: (R) -> RR): StateFlowDef<T, RR> {
    return StateFlowDef(parentStateFlow) {
        transform(transform(parentStateFlow.value))
    }
}

class StateFlowDef<T: Any?, R: Any?> (val parentStateFlow: StateFlow<T>, val transform: (T) -> R) {
    val value get() = transform(parentStateFlow.value)
}