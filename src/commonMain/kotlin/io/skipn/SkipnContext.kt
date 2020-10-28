@file:JvmMultifileClass
@file:JvmName("SkipnContext_")

package io.skipn

import io.skipn.notifiers.StatefulValue
import io.skipn.platform.SkipnResources
import kotlinx.html.FlowContent
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

class SkipnContext {

    lateinit var route: StatefulValue<String>
    val points = Elements()
    var isInitializing = true
    val resources = SkipnResources()

    fun init(route: String) {
        this.route = StatefulValue(route)
    }
}

class Elements {
    var compId = 0

    fun generateId() = "skipn-${compId++}"
    fun generateIdInt() = compId++
}

expect val FlowContent.skipnContext : SkipnContext