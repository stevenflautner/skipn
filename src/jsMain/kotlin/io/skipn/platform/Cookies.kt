package io.skipn.platform

import io.skipn.SkipnContext
import kotlinx.browser.document
import kotlin.js.Date

actual object Cookies {

    actual fun get(skipnContext: SkipnContext, name: String): String? {
        return getAll()[name]
    }

    private fun getAll(): Map<String, String> {
        return document.cookie.split(";").map {
            val cookie = it.trim()
            val keyVal = cookie.split("=")
            keyVal[0] to cookie
        }.toMap()
    }

    actual fun store(cookie: String) {
        document.cookie = cookie
    }

    actual fun delete(name: String) {
        document.cookie = "$name=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;"
    }

//    actual suspend fun onExpired(name: String, onExpired: () -> Unit) {
//        val cookie = getAll()[name]?.trim() ?: run {
//            onExpired()
//            return
//        }
//
//        val expiry = Date(
//            cookie.map {
//                val keyVal = cookie.split("=")
//                keyVal[0] to keyVal[1]
//            }.toMap()["Expires"] ?: run {
//                println("KOOOOKA")
//                println(cookie)
//                onExpired()
//                return
//            }
//        )
//        println("EXPPPSP")
//        println(expiry)
//
////        val expiry = Date()
////        while (true) {
////
////        }
//    }
}