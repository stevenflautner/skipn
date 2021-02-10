package io.skipn.builder

import io.skipn.actions.updateUrlParameter
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.max

actual class Router actual constructor(fullRoute: String) : RouterBase(fullRoute) {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    actual fun changeParameter(key: String, newValue: Any?) {
        if (newValue == null) {
            route.parameters.remove(key)
        } else {
            route.parameters[key] = newValue.toString()
        }

        updateUrlParameter(route.parameters.formUrlEncode())
        coroutineScope.launch {
            stream.emit(ParameterChange(key, newValue?.toString()))
        }
//        stream.tryEmit(ParameterChange(key, newValue?.toString()))
    }

    actual fun changeRoute(fullRoute: String) {
        val oldRoute = route
        val newRoute = ParsedRoute(fullRoute)
        route = newRoute

        updateRoute(oldRoute.routeValues, newRoute.routeValues)
        updateParameters(oldRoute.parameters, newRoute.parameters)
    }

    actual fun updateRoute(oldRouteValues: List<String>, newRouteValues: List<String>) {
        // Update route differences
        var foundRouteDifference = false

        for (i in 0 until max(oldRouteValues.size, newRouteValues.size)) {
            val oldRoute = oldRouteValues.getOrNull(i)
            val newRoute = newRouteValues.getOrNull(i)

            if (oldRoute != newRoute) {
                coroutineScope.launch {
                    stream.emit(RouteChange(i, newRoute))
                }
//                stream.tryEmit(RouteChange(i, newRoute))
                foundRouteDifference = true
                break
            }
        }

        if (foundRouteDifference) {
            // Reset scroll when route changed
            window.scrollTo(0.0, 0.0)
        }

        // If the two are identical up until the size of the new array,
        // then check if they are of the same size. If not, then there was a change of route
        if (!foundRouteDifference && oldRouteValues.size != newRouteValues.size) {
            // Reset scroll when route changed
            window.scrollTo(0.0, 0.0)
            coroutineScope.launch {
                stream.emit(RouteChange(newRouteValues.lastIndex, newRouteValues.last()))
            }
//            stream.tryEmit(RouteChange(newRouteValues.lastIndex, newRouteValues.last()))
        }
    }

    actual fun updateParameters(oldParameters: Parameters, newParameters: Parameters) {
        val oldEntries = oldParameters.entries
        val newEntries = newParameters.entries

        val allEntries = (oldEntries + newEntries).distinctBy { it.key }

        allEntries.forEach {
            val oldValue = oldParameters[it.key]
            val newValue = newParameters[it.key]

            if (oldValue != newValue) {
                coroutineScope.launch {
                    stream.emit(ParameterChange(it.key, newValue))
                }
            }
        }
    }

}