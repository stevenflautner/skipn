package io.skipn.builder

import io.skipn.SkipnContext
import io.skipn.ensureRunAfterInitialization
import io.skipn.observers.Scope
import io.skipn.provide.PinningContext

actual class BuildContext(
    val id: String,
    actual val skipnContext: SkipnContext,
    actual val pinningContext: PinningContext,
    private val routeLevel: Int
) {

    actual val scope = Scope()

    // Calls the function regularly if invoked after the initialization phase
    actual fun runBrowser(block: DeviceFunction) {
        ensureRunAfterInitialization {
            scope.block()
        }
    }

    actual fun runBrowserDesktop(block: DeviceFunction) {
        skipnContext.device.runOnDesktop(scope, block)
    }

    companion object {
        fun create(id: String, parent: BuildContext, routeLevel: Int): BuildContext {
            return BuildContext(
                id,
                parent.skipnContext,
                PinningContext(parent = parent.pinningContext),
                routeLevel,
            )
        }
        fun createRoot(skipnContext: SkipnContext): BuildContext {
            return BuildContext(
                "skipn-root",
                skipnContext,
                PinningContext(parent = null),
                0,
            )
        }
    }

    actual fun getRouteLevel() = routeLevel
}

//actual fun <T, FLOW : Flow<T>, RES> FlowContent.stateIn(
//    flow: FLOW,
//    initialValue: (FLOW) -> RES
//) {
//    flow.stateIn(buildContext.coroutineScope, SharingStarted.Eagerly, initialValue(flow))
//}