package io.skipn.utils

import io.skipn.platform.Cookies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

expect fun launchGlobal(body: suspend CoroutineScope.() -> Unit)