package io.skipn.events

import io.skipn.browser.BrowserFormElement
import kotlinx.html.FORM

expect fun FORM.onBrowserMounted(onMounted: (BrowserFormElement) -> Unit)