package io.skipn.builder

import io.ktor.http.*

actual fun String.encodeURLParameter(): String = encodeURLParameter(true)