package io.skipn.events

import io.skipn.form.FormState
import io.skipn.Endpoint
import io.skipn.FormEndpoint
import io.skipn.browser.BrowserElement
import io.skipn.form.FormBuilder
import io.skipn.prepareElement
import kotlinx.html.FORM
import kotlinx.html.FlowContent

actual fun FlowContent.onMounted(onMounted: (BrowserElement) -> Unit) {
    prepareElement()
}

actual fun FlowContent.onClick(ignoreChildren: Boolean, onClick: (() -> Unit)?) {
    if (onClick == null) return
    prepareElement()
}

actual fun FlowContent.onHover(onHover: (Boolean) -> Unit) {
    prepareElement()
    onHover(false)
}

actual fun FORM.preventDefaultSubmit() {
    prepareElement()
}

actual inline fun <reified RESP: Any> FORM.attachSubmitHandler(
        endpoint: FormEndpoint<*, RESP>,
        builder: FormBuilder<RESP>,
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