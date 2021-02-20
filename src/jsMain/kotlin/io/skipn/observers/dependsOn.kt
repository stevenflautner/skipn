package io.skipn.observers

import io.skipn.builder.buildContext
import io.skipn.builder.builder
import io.skipn.builder.currentRoute
import io.skipn.prepareElement
import io.skipn.skipnContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.HTMLTag

actual fun <T> HTMLTag.internalDependOn(flow: () -> Flow<T>): Flow<T> {
    if (dependenciesBuilt) {
        return dependencies!![depId++] as Flow<T>
    }
    if (!buildContextBuilt) {
        val vNode = prepareElement()

        // Creates a new Build Context
        // as a copy of the current one
        builder.createContextAndDescend(vNode)
        buildContextBuilt = true
    }
    return flow().also {
        addDependency(it)
    }
}

actual fun HTMLTag.dependOnRoute(): String? {
    if (dependenciesBuilt) {
        return (dependencies!![depId++] as StateFlow<String?>).value
    }
    if (buildContextBuilt) error("dependOnRoute was invoked after dependOn. Please make sure that dependOnRoute is invoked before any other dependOn in the tag")

    val parentContext = buildContext
    val route = parentContext.currentRoute
    val vNode = prepareElement()

    // Creates a new Build Context
    // as a copy of the current one
    builder.createContextAndDescend(vNode, parentContext.getRouteLevel() + 1)
    buildContextBuilt = true

    addDependency(route)

    return route.value
}