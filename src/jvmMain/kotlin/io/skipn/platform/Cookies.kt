package io.skipn.platform

import io.skipn.SkipnContext
import io.skipn.builder.BuildContext
import io.skipn.errors.BrowserOnlyFunction

actual object Cookies {

    actual fun get(skipnContext: SkipnContext, name: String): String? {
        return skipnContext.applicationCall.request.cookies[name]
    }

    actual fun store(cookie: String) {
        throw BrowserOnlyFunction
    }

    actual fun delete(name: String) {
        throw BrowserOnlyFunction
    }

//    actual suspend fun onExpired(name: String, onExpired: () -> Unit) {
//        throw BrowserOnlyFunction
//    }
}