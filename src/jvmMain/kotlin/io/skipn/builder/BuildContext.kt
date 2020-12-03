package io.skipn.builder

import io.ktor.http.*
import io.skipn.SkipnContext
import io.skipn.errors.BrowserOnlyFunction
import io.skipn.provide.PinningContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.html.FlowContent

actual class BuildContext actual constructor(
        id: String,
        skipnContext: SkipnContext,
        pinningContext: PinningContext
) : BuildContextBase(id, skipnContext, pinningContext) {
    actual companion object {
        actual fun create(id: String, parent: BuildContext): BuildContext {
            return BuildContext(
                    id,
                    parent.skipnContext,
                    PinningContext(parent = parent.pinningContext)
            )
        }
        actual fun createRoot(skipnContext: SkipnContext): BuildContext {
            return BuildContext(
                    "skipn-root",
                    skipnContext,
                    PinningContext(parent = null)
            )
        }
    }

    actual fun launch(block: suspend CoroutineScope.() -> Unit) {
        // Empty function, should have an empty body
    }
}

//actual fun <T, FLOW : Flow<T>, RES> FlowContent.stateIn(
//    flow: FLOW,
//    initialValue: (FLOW) -> RES
//) {
//    flow.stateIn(GlobalScope, SharingStarted.Eagerly, initialValue(flow))
//}