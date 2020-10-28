package io.skipn.platform

expect val platform: Platform
expect var DEV: Boolean

enum class Platform {
    BROWSER, SERVER
}