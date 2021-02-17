package io.skipn.builder

import VNode
import io.skipn.SkipnContext
import io.skipn.ensureRunAfterInitialization
import io.skipn.provide.PinningContext
import kotlinx.coroutines.*

actual class BuildContext(
    val vNode: VNode?,
    skipnContext: SkipnContext,
    pinningContext: PinningContext,
    private val routeLevel: Int,
    private var coroutineScope: CoroutineScope
) : BuildContextBase(skipnContext, pinningContext) {

    actual fun getCoroutineScope(): CoroutineScope = coroutineScope

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

    actual fun launchOnDesktop(block: suspend CoroutineScope.() -> Unit) {
//        val targetScope = coroutineScope
        skipnContext.device.runOnDesktop(this, block)


//        ensureRunAfterInitialization {
//            if (targetScope == coroutineScope) {
//                skipnContext.device.runOnDesktop(this) {
//                    targetScope.launch(block = block)
//                }
//            }
//        }
    }

    fun cancelAndCreateScope(parentScope: CoroutineScope) {
        // Cancel the current coroutine context
        // and replace it with a new one
        coroutineScope.coroutineContext.cancelChildren()

//        // Create new coroutine scope
//        // and rebuild the child tree with it
//        coroutineScope = CoroutineScope(SupervisorJob(parentScope.coroutineContext.job))
    }

    companion object {
        fun create(vNode: VNode, parent: BuildContext, routeLevel: Int): BuildContext {
            return BuildContext(
//                id,
                vNode,
                parent.skipnContext,
                PinningContext(parent = parent.pinningContext),
                routeLevel,
                CoroutineScope(SupervisorJob(parent.coroutineScope.coroutineContext.job))
            )
        }
        fun createRoot(skipnContext: SkipnContext): BuildContext {
            return BuildContext(
//                "skipn-root",
                null,
                skipnContext,
                PinningContext(parent = null),
                0,
                CoroutineScope(SupervisorJob())
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