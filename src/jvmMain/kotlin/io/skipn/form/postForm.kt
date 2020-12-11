package io.skipn.form

import io.skipn.errors.BrowserOnlyFunction

actual suspend inline fun <reified RESP : Any> postForm(state: FormState<RESP>): RESP {
    throw BrowserOnlyFunction
}