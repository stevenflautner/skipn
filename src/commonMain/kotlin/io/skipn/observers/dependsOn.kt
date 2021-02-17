@file:JvmMultifileClass
@file:JvmName("_dependsOn")

package io.skipn.observers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.HTMLTag
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

fun <T> HTMLTag.dependOn(stateFlow: () -> StateFlow<T>): T {
    val flow = dependOn(stateFlow as () -> Flow<T>)
    return (flow as StateFlow<T>).value
}

expect fun <T> HTMLTag.dependOn(flow: () -> Flow<T>): Flow<T>