package io.skipn.observers

import kotlinx.coroutines.flow.Flow
import kotlinx.html.HTMLTag

actual fun <T> HTMLTag.dependOn(flow: () -> Flow<T>): Flow<T> {
    // Empty function should not have a body
    return flow()
}