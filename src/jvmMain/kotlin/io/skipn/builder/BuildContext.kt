package io.skipn.builder

import io.skipn.SkipnContext
import io.skipn.errors.BrowserOnlyFunction
import io.skipn.observers.Scope
import io.skipn.provide.PinningContext
import kotlinx.coroutines.CoroutineScope

actual class BuildContext(
        id: String,
        actual val skipnContext: SkipnContext,
        actual val pinningContext: PinningContext,
        internal var routeLevel: Int,
) {

    actual val scope = Scope()

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

    actual fun runBrowser(block: DeviceFunction) {
        // Empty function, should have an empty body
    }

    actual fun runBrowserDesktop(block: DeviceFunction) {
        // Empty function, should have an empty body
    }

    actual fun getRouteLevel() = routeLevel
}