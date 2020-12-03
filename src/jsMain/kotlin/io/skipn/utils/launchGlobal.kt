package io.skipn.utils

import io.skipn.ensureRunAfterInitialization
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

actual fun launchGlobal(body: suspend CoroutineScope.() -> Unit) {
    ensureRunAfterInitialization {
        GlobalScope.launch(block = body)
    }
}