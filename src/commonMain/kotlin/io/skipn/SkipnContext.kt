@file:JvmMultifileClass
@file:JvmName("SkipnContext_")

package io.skipn

import io.skipn.builder.buildContext
import io.skipn.platform.SkipnResources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.html.FlowContent
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

class Route(val route: String, val oldRoute: String? = null)

expect class SkipnContext {

    var isInitializing: Boolean
    var route: MutableStateFlow<Route>
    val points: Elements
    val resources: SkipnResources

}

class Elements {
    var compId = 0

    fun generateId() = "skipn-${compId++}"
    fun generateIdInt() = compId++
}

val FlowContent.skipnContext: SkipnContext
    get() = buildContext.skipnContext