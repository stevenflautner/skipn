package io.skipn.observers

import io.skipn.state.State
import io.skipn.state.toState
import io.skipn.state.transform

//import kotlinx.coroutines.flow.StateFlow
//
fun <T, R> State<T>.mapState(transform: (T) -> R): State<R> {
    return this.transform<T, R> {
        emit(transform(it))
    }.toState(transform(value))
}
//
//fun <T: Any?, R: Any?, RR: Any?> StateFlowDef<T, R>.mapState(transform: (R) -> RR): StateFlowDef<T, RR> {
//    return StateFlowDef(parentStateFlow) {
//        transform(transform(parentStateFlow.value))
//    }
//}
//
//class StateFlowDef<T: Any?, R: Any?> (val parentStateFlow: StateFlow<T>, val transform: (T) -> R) {
//    val value get() = transform(parentStateFlow.value)
//}