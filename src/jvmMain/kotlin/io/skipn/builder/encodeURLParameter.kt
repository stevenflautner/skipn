package io.skipn.builder

import io.ktor.http.*

actual fun String.encodeURLParameter(): String = encodeURLParameter(spaceToPlus = true)
actual fun String.decodeURLParameter(): String = decodeURLQueryComponent(plusIsSpace = true)