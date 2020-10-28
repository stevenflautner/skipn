package io.skipn.events

import io.skipn.browser.BrowserFormElement
import io.skipn.skipnContext
import io.skipn.prepareElement
import kotlinx.browser.document
import kotlinx.html.FORM
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit

actual fun FORM.onBrowserMounted(onMounted: (BrowserFormElement) -> Unit) {
    val element = prepareElement() as HTMLFormElement

    if (skipnContext.isInitializing) {
        onMounted(BrowserFormElement(element))
    } else {
        MutationObserver { records, observer ->
            if (document.contains(element)) {
                onMounted(BrowserFormElement(element))
                observer.disconnect()
            }
        }.observe(document, MutationObserverInit(subtree = true, childList = true))
    }
}