package io.skipn.builder

actual fun String.encodeURLParameter(): String = encodeURIComponent(this)

external fun encodeURIComponent(component: String): String