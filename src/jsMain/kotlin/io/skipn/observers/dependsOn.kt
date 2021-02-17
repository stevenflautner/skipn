package io.skipn.observers

import io.skipn.builder.builder
import io.skipn.prepareElement
import kotlinx.coroutines.flow.Flow
import kotlinx.html.HTMLTag

actual fun <T> HTMLTag.dependOn(flow: () -> Flow<T>): Flow<T> {
    if (dependenciesBuilt) {
        return dependencies!![depId++] as Flow<T>
    }
    if (dependencies == null) {
        val vNode = prepareElement()

        // Creates a new Build Context
        // as a copy of the current one
        builder.createContextAndDescend(vNode)
    }
    return flow().also {
        addDependency(it)
    }
}