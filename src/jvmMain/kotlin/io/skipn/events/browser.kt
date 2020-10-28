package io.skipn.events

import io.skipn.form.FormState
import io.skipn.Endpoint
import io.skipn.browser.BrowserElement
import io.skipn.prepareElement
import kotlinx.html.FORM
import kotlinx.html.FlowContent

actual fun FlowContent.onMounted(onMounted: (BrowserElement) -> Unit) {
    prepareElement()
}

actual fun FlowContent.onClick(ignoreChildren: Boolean, onClick: () -> Unit) {
    prepareElement()
}

actual fun FlowContent.onHover(onHover: (Boolean) -> Unit) {
    prepareElement()
    onHover(false)
}

actual inline fun <reified RESP: Any> FORM.submitHandler(
        endpoint: Endpoint<*, RESP>,
        formState: FormState,
        crossinline onSuccess: (RESP) -> Unit
) : () -> Unit {
    prepareElement()
    return {}
}

actual fun FlowContent.onInput(onInput: (String, BrowserElement) -> Unit) {
    prepareElement()
}

actual fun FlowContent.onDispose(onDispose: (BrowserElement) -> Unit) {
    prepareElement()
}

actual fun FlowContent.onScroll(onScroll: (BrowserElement) -> Unit) {
    prepareElement()
}