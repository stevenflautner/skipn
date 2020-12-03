package io.skipn.platform

import io.skipn.SkipnContext
import io.skipn.builder.BuildContext

expect object Cookies {
    fun get(skipnContext: SkipnContext, name: String): String?

    fun store(cookie: String)
    fun delete(name: String)
//    suspend fun onExpired(name: String, onExpired: () -> Unit)
}

fun BuildContext.getCookie(name: String) = Cookies.get(skipnContext, name)
fun storeCookie(cookie: String) = Cookies.store(cookie)
fun deleteCookie(name: String) = Cookies.delete(name)