@file:JvmMultifileClass
@file:JvmName("BuildContext_")

package io.skipn.builder

import io.skipn.SkipnContext
import io.skipn.provide.PinningContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.html.FlowContent
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

expect class BuildContext constructor(id: String, skipnContext: SkipnContext, pinningContext: PinningContext) : BuildContextBase {

    fun launch(block: suspend CoroutineScope.() -> Unit)

    companion object {
        fun create(id: String, parent: BuildContext): BuildContext
        fun createRoot(skipnContext: SkipnContext): BuildContext
    }
}

val FlowContent.buildContext: BuildContext
    get() = builder.currentBuildContext

fun FlowContent.launch(block: suspend CoroutineScope.() -> Unit) = buildContext.launch(block)

//expect fun <T: Any?, FLOW: Flow<T>, RES: Any?> FlowContent.stateIn(flow: FLOW, initialValue: (FLOW) -> RES)