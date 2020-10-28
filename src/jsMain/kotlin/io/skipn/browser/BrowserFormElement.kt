package io.skipn.browser

import org.w3c.dom.HTMLFormElement

actual class BrowserFormElement(private val form: HTMLFormElement) : BrowserElement(form) {
    actual fun submit() {
        form.submit()
    }
}