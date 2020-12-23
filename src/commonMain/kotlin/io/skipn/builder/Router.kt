@file:JvmMultifileClass
@file:JvmName("Router_")

package io.skipn.builder

import io.ktor.http.*
import io.skipn.actions.updateUrlParameter
import io.skipn.skipnContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.html.FlowContent
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.math.max

class Router(fullRoute: String) {

    private var routeArray: List<String>
    private var parameters: Parameters

    init {
        val url = URLBuilder(fullRoute)
        routeArray = parseRoute(url.encodedPath)
        parameters = url.parameters.build()
    }

    fun routeFor(level: Int) = routeArray.getOrNull(level)?.let {
        if (it == "/") null
        else it
    }

    fun parameterFor(key: String) = parameters[key]

    private val stream = MutableSharedFlow<Change>(extraBufferCapacity = 1)

    private fun parseRoute(_fullRoute: String): List<String> {
        val fullRoute = _fullRoute.removePrefix("/")
        val count = fullRoute.count { it == '/' }

        return if (count == 0) {
            listOf(fullRoute)
        } else {
            fullRoute.split("/")
        }
    }

    fun changeParameter(key: String, newValue: String?) {
        val parametersBuilder = ParametersBuilder().apply {
            parameters.forEach { s, list ->
                appendAll(s, list)
            }
        }

        if (newValue == null) {
            parametersBuilder.remove(key)
        } else {
            parametersBuilder[key] = newValue
        }

        val newParameters = parametersBuilder.build().also {
            parameters = it
        }

        updateUrlParameter(newParameters.formUrlEncode())
        stream.tryEmit(ParameterChange(key, newValue))
    }

    fun changeRoute(urlPath: String) {
        val url = URLBuilder(urlPath)
        updateParameters(url)
        updateRoute(url)
    }

    private fun updateRoute(url: URLBuilder) {
        val oldRouteArray = routeArray
        val newRouteArray = parseRoute(url.encodedPath).also {
            routeArray = it
        }

        // Update route differences
        var foundRouteDifference = false

        for (i in 0 until max(oldRouteArray.size, newRouteArray.size)) {
            val oldRoute = oldRouteArray.getOrNull(i)
            val newRoute = newRouteArray.getOrNull(i)

            if (oldRoute != newRoute) {
                stream.tryEmit(RouteChange(i, newRoute))
                foundRouteDifference = true
                break
            }
        }

        // If the two are identical up until the size of the new array,
        // then check if they are of the same size. If not, then there was a change of route
        if (!foundRouteDifference && oldRouteArray.size != newRouteArray.size) {
            stream.tryEmit(RouteChange(newRouteArray.lastIndex, newRouteArray.last()))
        }
    }

    private fun updateParameters(url: URLBuilder) {
        val oldParameters = parameters

        val newParameters = url.parameters.build().also {
            parameters = it
        }

        val oldEntries = oldParameters.entries()
        val newEntries = newParameters.entries()

        val allEntries = (oldEntries + newEntries).distinct()

        allEntries.forEach {
            val oldValue = oldParameters[it.key]
            val newValue = newParameters[it.key]

            if (oldValue != newValue) {
                stream.tryEmit(ParameterChange(it.key, newValue))
            }
        }
    }

    fun filterRouteChangesFor(level: Int) = stream.filterIsInstance<RouteChange>().filter { it.level == level }.map { it.route }
    fun filterParameterChangesFor(key: String) = stream.filterIsInstance<ParameterChange>().filter { it.key == key }.map { it.value }
}

interface Change
class RouteChange(val level: Int, val route: String?) : Change
class ParameterChange(val key: String, val value: String?) : Change

val FlowContent.router: Router
    get() = skipnContext.router

val BuildContext.currentRoute: StateFlow<String?>
    get() = skipnContext.router.filterRouteChangesFor(getRouteLevel()).stateIn(GlobalScope, SharingStarted.Lazily, skipnContext.router.routeFor(getRouteLevel()))


val FlowContent.currentRoute: StateFlow<String?> get() = buildContext.currentRoute