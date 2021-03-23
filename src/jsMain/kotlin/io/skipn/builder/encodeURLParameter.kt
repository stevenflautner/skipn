package io.skipn.builder

actual fun String.encodeURLParameter(): String = encodeURIComponent(this)
actual fun String.decodeURLParameter(): String = decodeURIComponent(this)

external fun encodeURIComponent(component: String): String
external fun decodeURIComponent(component: String): String
