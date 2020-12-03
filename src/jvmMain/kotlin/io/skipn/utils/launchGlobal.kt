package io.skipn.utils

import kotlinx.coroutines.CoroutineScope

actual fun launchGlobal(body: suspend CoroutineScope.() -> Unit) {
    // Empty Function should not be BrowserOnlyFunction
}