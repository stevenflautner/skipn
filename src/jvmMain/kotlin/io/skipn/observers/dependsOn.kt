package io.skipn.observers

import io.skipn.builder.buildContext
import io.skipn.builder.builder
import io.skipn.skipnContext
import kotlinx.coroutines.flow.Flow
import kotlinx.html.HTMLTag

actual fun <T> HTMLTag.internalDependOn(flow: () -> Flow<T>): Flow<T> {
    // Empty function should not have a body
    return flow()
}

actual fun HTMLTag.dependOnRoute(): String? {
    val route = skipnContext.router.routeFor(buildContext.routeLevel)
    builder.descendRoute(this)
    return route
}