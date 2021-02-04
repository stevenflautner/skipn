@file:JvmMultifileClass
@file:JvmName("Router_")

package io.skipn.builder

import io.skipn.skipnContext
import io.skipn.state.*
import kotlinx.html.FlowContent
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

abstract class RouterBase(fullRoute: String) {
    protected var route = ParsedRoute(fullRoute)

    val stream = streamOf<Change>()

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

    fun parameter(key: String, context: BuildContext) = stream.filterIsInstance<ParameterChange>()
        .filter { it.key == key }
        .map { it.value }
        .toState(getParameterValue(key))
}

expect class Router(fullRoute: String) : RouterBase {
    fun changeParameter(key: String, newValue: Any?)

    fun changeRoute(fullRoute: String)

    fun updateRoute(oldRouteValues: List<String>, newRouteValues: List<String>)

    fun updateParameters(oldParameters: Parameters, newParameters: Parameters)
}
//
//class Router(fullRoute: String) {
//
//    private var route = ParsedRoute(fullRoute)
//
//    fun routeFor(level: Int) = route.routeValues.getOrNull(level)?.let {
//        if (it == "/") null
//        else it
//    }
//
//    fun getParameterValue(key: String) = route.parameters[key]
//
//    val stream = streamOf<Change>()
//
//    fun changeParameter(key: String, newValue: Any?) {
//        if (newValue == null) {
//            route.parameters.remove(key)
//        } else {
//            route.parameters[key] = newValue.toString()
//        }
//
//        updateUrlParameter(route.parameters.formUrlEncode())
//        stream.emit(ParameterChange(key, newValue?.toString()))
//    }
//
//    fun changeRoute(fullRoute: String) {
//        route = ParsedRoute(fullRoute).apply {
//            updateRoute(routeValues)
//            updateParameters(parameters)
//        }
//    }
//
//    private fun updateRoute(newRouteValues: List<String>) {
//        val oldRouteValues = route.routeValues
//
//        // Update route differences
//        var foundRouteDifference = false
//
//        for (i in 0 until max(oldRouteValues.size, newRouteValues.size)) {
//            val oldRoute = oldRouteValues.getOrNull(i)
//            val newRoute = newRouteValues.getOrNull(i)
//
//            if (oldRoute != newRoute) {
//                stream.emit(RouteChange(i, newRoute))
//                foundRouteDifference = true
//                break
//            }
//        }
//
//        // If the two are identical up until the size of the new array,
//        // then check if they are of the same size. If not, then there was a change of route
//        if (!foundRouteDifference && oldRouteValues.size != newRouteValues.size) {
//            stream.emit(RouteChange(newRouteValues.lastIndex, newRouteValues.last()))
//        }
//    }
//
//    private fun updateParameters(newParameters: Parameters) {
//        val oldParameters = route.parameters
//
//        val oldEntries = oldParameters.entries
//        val newEntries = newParameters.entries
//
//        val allEntries = (oldEntries + newEntries).distinct()
//
//        allEntries.forEach {
//            val oldValue = oldParameters[it.key]
//            val newValue = newParameters[it.key]
//
//            if (oldValue != newValue) {
//                stream.emit(ParameterChange(it.key, newValue))
//            }
//        }
//    }
//
//
//}

interface Change
class RouteChange(val level: Int, val route: String?) : Change
class ParameterChange(val key: String, val value: String?) : Change

val FlowContent.router: Router
    get() = skipnContext.router

val BuildContext.currentRoute: State<String?>
    get() = skipnContext.router.filterRouteChangesFor(getRouteLevel())
        .toState(skipnContext.router.routeFor(getRouteLevel()))

fun BuildContext.parameter(key: String) = skipnContext.router.parameter(key, this)

val FlowContent.currentRoute: State<String?> get() = buildContext.currentRoute