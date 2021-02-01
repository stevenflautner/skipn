@file:JvmMultifileClass
@file:JvmName("SkipnContext_")

package io.skipn

import io.skipn.builder.Router
import io.skipn.builder.buildContext
import io.skipn.platform.SkipnResources
import kotlinx.html.FlowContent
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

class Route(val route: String, val oldRoute: String? = null)

expect class SkipnContext: SkipnContextBase {

    var isInitializing: Boolean
    val points: Elements
    val resources: SkipnResources

}

abstract class SkipnContextBase(route: String) {
    val router = Router(route)
}

class Elements {
    var compId = 0

    fun generateId() = "skipn-${compId++}"
    fun generateIdInt() = compId++
}

val FlowContent.skipnContext: SkipnContext
    get() = buildContext.skipnContext