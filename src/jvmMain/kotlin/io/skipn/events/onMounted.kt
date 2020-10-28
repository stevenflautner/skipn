package io.skipn.events

import io.skipn.browser.BrowserFormElement
import io.skipn.prepareElement
import kotlinx.html.FORM

actual fun FORM.onBrowserMounted(onMounted: (BrowserFormElement) -> Unit) {
    prepareElement()
}