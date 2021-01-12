package io.skipn.builder

import io.skipn.SkipnContext
import io.skipn.provide.PinningContext
import kotlinx.coroutines.CoroutineScope

actual class BuildContext(
        id: String,
        skipnContext: SkipnContext,
        pinningContext: PinningContext,
        internal var routeLevel: Int
) : BuildContextBase(id, skipnContext, pinningContext) {
    companion object {
        fun createRoot(skipnContext: SkipnContext): BuildContext {
            return BuildContext(
                "skipn-root",
                skipnContext,
                PinningContext(parent = null),
                0
            )
        }
    }

    actual fun launchOnDesktop(block: suspend CoroutineScope.() -> Unit) {
        // Empty function, should have an empty body
    }

    actual fun launch(block: suspend CoroutineScope.() -> Unit) {
        // Empty function, should have an empty body
    }

    actual fun getRouteLevel() = routeLevel
}

//actual fun <T, FLOW : Flow<T>, RES> FlowContent.stateIn(
//    flow: FLOW,
//    initialValue: (FLOW) -> RES
//) {
//    flow.stateIn(GlobalScope, SharingStarted.Eagerly, initialValue(flow))
//}