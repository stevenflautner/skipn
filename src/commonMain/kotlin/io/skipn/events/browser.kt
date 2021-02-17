package io.skipn.events

import io.skipn.browser.BrowserElement
import io.skipn.FormEndpoint
import io.skipn.form.FormBuilder
import kotlinx.html.FORM
import kotlinx.html.FlowContent

expect inline fun FlowContent.onEvent(name: String, crossinline onEvent: ((BrowserEvent) -> Unit))

expect fun FlowContent.onMounted(onMounted: (BrowserElement) -> Unit)
expect fun FlowContent.onClick(ignoreChildren: Boolean = false, onClick: ((BrowserEvent) -> Unit)? = null)
expect fun FlowContent.onKeyUp(onKeyUp: (String) -> Unit)
expect fun FlowContent.onHover(onHover: (Boolean) -> Unit)
expect inline fun <reified RESP: Any> FORM.attachSubmitHandler(
        endpoint: FormEndpoint<*, RESP>,
        builder: FormBuilder<RESP>,
        crossinline onSuccess: (RESP) -> Unit
) : () -> Unit
expect fun FlowContent.onInput(onInput: (String, BrowserElement) -> Unit)
expect fun FlowContent.onChange(onChange: (BrowserEvent) -> Unit)
expect fun FlowContent.onDispose(onDispose: (BrowserElement) -> Unit)
expect fun FlowContent.onScroll(onScroll: (BrowserElement) -> Unit)
expect fun FORM.preventDefaultSubmit()