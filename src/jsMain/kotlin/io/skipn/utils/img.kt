package io.skipn.utils

import io.skipn.platform.DEV

actual fun imgSrcTransform(src: String?): String? {
    if (src == null) return null

    return if (DEV && src.startsWith("/")) {
        Api.devUrl + src
    } else src
}