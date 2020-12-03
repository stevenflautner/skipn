package io.skipn.builder

import io.skipn.SkipnContext
import io.skipn.ensureRunAfterInitialization
import io.skipn.provide.PinningContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.html.FlowContent

actual class BuildContext actual constructor(
        id: String,
        skipnContext: SkipnContext,
        pinningContext: PinningContext
) : BuildContextBase(id, skipnContext, pinningContext) {

    lateinit var coroutineScope: CoroutineScope

    // Launches coroutine only if the current coroutineScope did not
    // change while the initialization process ran
    // Calls the function regularly if invoked after the initialization phase
    actual fun launch(block: suspend CoroutineScope.() -> Unit) {
        val targetScope = coroutineScope
        ensureRunAfterInitialization {
            if (targetScope == coroutineScope) {
                targetScope.launch(block = block)
            }
        }
    }

    fun cancelAndCreateScope(parentScope: CoroutineScope) {
        // Cancel the current coroutine context
        // and replace it with a new one
        coroutineScope.cancel()

        // Create new coroutine scope
        // and rebuild the child tree with it
        coroutineScope = CoroutineScope(SupervisorJob(parentScope.coroutineContext.job))
    }

    actual companion object {
        actual fun create(id: String, parent: BuildContext): BuildContext {
            return BuildContext(
                    id,
                    parent.skipnContext,
                    PinningContext(parent = parent.pinningContext)
            ).apply {
                coroutineScope = CoroutineScope(SupervisorJob(parent.coroutineScope.coroutineContext.job))
            }
        }
        actual fun createRoot(skipnContext: SkipnContext): BuildContext {
            return BuildContext(
                    "skipn-root",
                    skipnContext,
                    PinningContext(parent = null)
            ).apply {
                coroutineScope = CoroutineScope(SupervisorJob())
            }
        }
    }
}

//actual fun <T, FLOW : Flow<T>, RES> FlowContent.stateIn(
//    flow: FLOW,
//    initialValue: (FLOW) -> RES
//) {
//    flow.stateIn(buildContext.coroutineScope, SharingStarted.Eagerly, initialValue(flow))
//}