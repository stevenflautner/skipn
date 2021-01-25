@file:JvmMultifileClass
@file:JvmName("Router_")

package io.skipn.builder

import io.ktor.http.*
import io.skipn.actions.updateUrlParameter
import io.skipn.skipnContext
import io.skipn.state.StatefulValue
import io.skipn.utils.mutableStateFlowOf
import kotlinx.coroutines.CoroutineScope
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

    fun getParameterValue(key: String) = parameters[key]

    val stream = MutableSharedFlow<Change>(extraBufferCapacity = 2)

    private fun parseRoute(_fullRoute: String): List<String> {
        val fullRoute = _fullRoute.removePrefix("/")
        val count = fullRoute.count { it == '/' }

        return if (count == 0) {
            listOf(fullRoute)
        } else {
            fullRoute.split("/")
        }
    }

    fun changeParameter(key: String, newValue: Any?) {
        val parametersBuilder = ParametersBuilder().apply {
            parameters.forEach { s, list ->
                appendAll(s, list)
            }
        }

        if (newValue == null) {
            parametersBuilder.remove(key)
        } else {
            parametersBuilder[key] = newValue.toString()
        }

        val newParameters = parametersBuilder.build().also {
            parameters = it
        }

        updateUrlParameter(newParameters.formUrlEncode())
        stream.tryEmit(ParameterChange(key, newValue?.toString()))
    }

    fun changeRoute(urlPath: String) {
        val url = URLBuilder(urlPath)
        updateRoute(url)
        updateParameters(url)
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

        val a = mutableStateFlowOf(12)
        a.asStateFlow().value

    }

    fun filterRouteChangesFor(level: Int) =
        stream.filterIsInstance<RouteChange>().filter { it.level == level }.map { it.route }

    fun filterParameterChangesFor(key: String) = stream.filterIsInstance<ParameterChange>()
        .filter { it.key == key }
        .map { it.value }

    fun parameter(key: String, scope: CoroutineScope) = stream.filterIsInstance<ParameterChange>()
        .filter { it.key == key }
        .map { it.value }
        .stateIn(scope, SharingStarted.Eagerly, getParameterValue(key))
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