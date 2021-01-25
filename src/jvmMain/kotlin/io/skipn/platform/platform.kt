package io.skipn.platform

actual val platform: Platform = Platform.SERVER
actual var DEV: Boolean = System.getProperty("DEV")?.toBoolean() ?: false