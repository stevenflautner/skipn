package io.skipn.builder

import io.skipn.actions.updateUrlParameter
import kotlin.math.max

actual class Router actual constructor(fullRoute: String) : RouterBase(fullRoute) {

    actual fun changeParameter(key: String, newValue: Any?) {
        if (newValue == null) {
            route.parameters.remove(key)
        } else {
            route.parameters[key] = newValue.toString()
        }

        updateUrlParameter(route.parameters.formUrlEncode())
        stream.emit(ParameterChange(key, newValue?.toString()))
    }

    actual fun changeRoute(fullRoute: String) {
        route = ParsedRoute(fullRoute).apply {
            updateRoute(routeValues)
            updateParameters(parameters)
        }
    }

    actual fun updateRoute(newRouteValues: List<String>) {
        val oldRouteValues = route.routeValues

        // Update route differences
        var foundRouteDifference = false

        for (i in 0 until max(oldRouteValues.size, newRouteValues.size)) {
            val oldRoute = oldRouteValues.getOrNull(i)
            val newRoute = newRouteValues.getOrNull(i)

            if (oldRoute != newRoute) {
                stream.emit(RouteChange(i, newRoute))
                foundRouteDifference = true
                break
            }
        }

        // If the two are identical up until the size of the new array,
        // then check if they are of the same size. If not, then there was a change of route
        if (!foundRouteDifference && oldRouteValues.size != newRouteValues.size) {
            stream.emit(RouteChange(newRouteValues.lastIndex, newRouteValues.last()))
        }
    }

    actual fun updateParameters(newParameters: Parameters) {
        val oldParameters = route.parameters

        val oldEntries = oldParameters.entries
        val newEntries = newParameters.entries

        val allEntries = (oldEntries + newEntries).distinct()

        allEntries.forEach {
            val oldValue = oldParameters[it.key]
            val newValue = newParameters[it.key]

            if (oldValue != newValue) {
                stream.emit(ParameterChange(it.key, newValue))
            }
        }
    }

}