@file:JvmMultifileClass
@file:JvmName("BuildContext_")

package io.skipn.builder

import io.skipn.SkipnContext
import io.skipn.observers.Scope
import io.skipn.provide.PinningContext
import io.skipn.state.Stream
import kotlinx.html.FlowContent
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

typealias DeviceFunction = Scope.() -> Unit

expect class BuildContext {

    val skipnContext: SkipnContext
    val pinningContext: PinningContext
    val scope: Scope

    fun runBrowser(block: DeviceFunction)

    fun runBrowserDesktop(block: DeviceFunction)

    fun getRouteLevel(): Int

}

val FlowContent.buildContext: BuildContext
    get() = builder.getBuildContext()

fun FlowContent.runBrowser(block: DeviceFunction) {
    buildContext.runBrowser(block)
}

inline fun <T> Scope.observe(stream: Stream<T>, crossinline onChanged: (T) -> Unit) {
    val observer = stream.observe {
        onChanged(it)
    }
    onDispose {
        stream.removeObserver(observer)
    }
}