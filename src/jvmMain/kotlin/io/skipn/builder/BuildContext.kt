package io.skipn.builder

import io.skipn.SkipnContext
import io.skipn.errors.BrowserOnlyFunction
import io.skipn.provide.PinningContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

actual class BuildContext(
        skipnContext: SkipnContext,
        pinningContext: PinningContext,
        internal var routeLevel: Int,
        private val coroutineScope: CoroutineScope,
) : BuildContextBase(skipnContext, pinningContext) {

    actual fun getCoroutineScope() = coroutineScope

    companion object {
        fun createRoot(skipnContext: SkipnContext): BuildContext {
            return BuildContext(
                skipnContext,
                PinningContext(parent = null),
                0,
                CoroutineScope(SupervisorJob())
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