package io.skipn

import io.ktor.application.*

typealias ErrorFilter = suspend (Exception, ApplicationCall) -> Unit