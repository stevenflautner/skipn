@file:JvmMultifileClass
@file:JvmName("BuildContext_")

package io.skipn.builder

import kotlinx.coroutines.CoroutineScope
import kotlinx.html.FlowContent
import kotlinx.html.Tag
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

expect class BuildContext : BuildContextBase {

    fun launch(block: suspend CoroutineScope.() -> Unit)

    fun launchOnDesktop(block: suspend CoroutineScope.() -> Unit)

    fun getRouteLevel(): Int

    fun getCoroutineScope(): CoroutineScope

}

val Tag.buildContext: BuildContext
    get() = builder.currentBuildContext

fun FlowContent.launch(block: suspend CoroutineScope.() -> Unit) = buildContext.launch(block)



fun FlowContent.launchOnDesktop(block: suspend CoroutineScope.() -> Unit) = buildContext.launch(block)

//expect fun <T: Any?, FLOW: Flow<T>, RES: Any?> FlowContent.stateIn(flow: FLOW, initialValue: (FLOW) -> RES)