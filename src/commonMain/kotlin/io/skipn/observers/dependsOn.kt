@file:JvmMultifileClass
@file:JvmName("_dependsOn")

package io.skipn.observers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.HTMLTag
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

fun <T> HTMLTag.dependOn(stateFlow: () -> StateFlow<T>): T {
    val flow = internalDependOn(stateFlow)
    return (flow as StateFlow<T>).value
}

expect fun <T> HTMLTag.internalDependOn(flow: () -> Flow<T>): Flow<T>

expect fun HTMLTag.dependOnRoute(): String?

fun <T> HTMLTag.dependOn(flow: () -> Flow<T>) {
    internalDependOn(flow)
}