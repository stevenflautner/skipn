@file:JvmMultifileClass
@file:JvmName("Router_")

package io.skipn.builder

import io.skipn.skipnContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.html.FlowContent
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

abstract class RouterBase(fullRoute: String) {
    protected var route = ParsedRoute(fullRoute)

    val stream = MutableSharedFlow<Change>(extraBufferCapacity = 2)

    fun routeFor(level: Int) = route.routeValues.getOrNull(level)?.let {
        if (it == "/") null
        else it
    }

    fun getParameterValue(key: String) = route.parameters[key]

    fun filterRouteChangesFor(level: Int) =
        stream.filterIsInstance<RouteChange>().filter { it.level == level }.map { it.route }

    fun filterParameterChangesFor(key: String) = stream.filterIsInstance<ParameterChange>()
        .filter { it.key == key }
        .map { it.value }

    fun parameter(key: String, scope: CoroutineScope) = stream.filterIsInstance<ParameterChange>()
        .filter { it.key == key }
        .map { it.value }
        .stateIn(scope, SharingStarted.Lazily, getParameterValue(key))
}

expect class Router(fullRoute: String) : RouterBase {
    fun changeParameter(key: String, newValue: Any?)

    fun changeRoute(fullRoute: String)

    fun updateRoute(oldRouteValues: List<String>, newRouteValues: List<String>)

    fun updateParameters(oldParameters: Parameters, newParameters: Parameters)
}

interface Change
class RouteChange(val level: Int, val route: String?) : Change
class ParameterChange(val key: String, val value: String?) : Change

val FlowContent.router: Router
    get() = skipnContext.router

val BuildContext.currentRoute: StateFlow<String?>
    get() = skipnContext.router.filterRouteChangesFor(getRouteLevel()).stateIn(GlobalScope, SharingStarted.Lazily, skipnContext.router.routeFor(getRouteLevel()))

fun BuildContext.parameter(key: String) = skipnContext.router.parameter(key, getCoroutineScope())

val FlowContent.currentRoute: StateFlow<String?> get() = buildContext.currentRoute