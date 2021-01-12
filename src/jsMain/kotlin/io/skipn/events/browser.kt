package io.skipn.events

import io.skipn.*
import io.skipn.browser.BrowserElement
import io.skipn.builder.builder
import io.skipn.builder.launch
import io.skipn.form.FormBuilder
import kotlinx.browser.document
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.html.FORM
import kotlinx.html.FlowContent
import org.w3c.dom.GlobalEventHandlers
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.xhr.FormData
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

actual fun FlowContent.onMounted(onMounted: (BrowserElement) -> Unit) {
    val element = prepareElement()

    if (skipnContext.isInitializing) {
        onMounted(BrowserElement(element))
    } else {
        MutationObserver { records, observer ->
            if (document.contains(element)) {
                onMounted(BrowserElement(element))
                observer.disconnect()
            }
        }.observe(document, MutationObserverInit(subtree = true, childList = true))
    }
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual fun FlowContent.onClick(ignoreChildren: Boolean, onClick: (() -> Unit)?) {
    if (onClick == null) return
    val elem = prepareElement() as GlobalEventHandlers

    elem.onclick = {
        if (it.target != elem && ignoreChildren) null
        else onClick()
    }
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual fun FlowContent.onHover(onHover: (Boolean) -> Unit) {
    val elem = prepareElement() as GlobalEventHandlers

    var hovering = false

    elem.onmouseenter = {
        if (!hovering) {
            hovering = true
            onHover(hovering)
        }
    }
    elem.onmouseleave = {
        if (hovering) {
            hovering = false
            onHover(hovering)
        }
    }
}

class JSIterator<T>(private val jsObject: dynamic) : AbstractIterator<T>() {
    override fun computeNext() {
        val n = jsObject.next()
        if (n.done == null || n.done == false)
            setNext(n.value)
        else
            done()
    }
}
fun FormData.keys() = JSIterator<String>(this.asDynamic().keys())

actual fun FORM.preventDefaultSubmit() {
    val form = prepareElement() as HTMLFormElement
    form.onsubmit = {
        it.preventDefault()
    }
}

actual inline fun <reified RESP: Any> FORM.attachSubmitHandler(
        endpoint: FormEndpoint<*, RESP>,
        builder: FormBuilder<RESP>,
        crossinline onSuccess: (RESP) -> Unit
) : () -> Unit {
//    val form = prepareElement() as HTMLFormElement
//
//    val submit = {
//        if (builder.validateAll()) {
////            GlobalScope.launch {
////                api.post<RESP>(endpoint.route) {
////
////                }
////            }
//            postFormLegacyApi(endpoint, form, onSuccess)
//        }
//    }
//
//    form.onsubmit = { e ->
//        e.preventDefault()
//        submit()
//    }
//    return submit
    return {}
}

//@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
//actual fun FlowContent.onChange(onChange: (String?) -> Unit) {
//    val elem = prepareElement()
//    elem as GlobalEventHandlers
//
//    elem.onchange = { e ->
//        onChange(e.data)
//    }
//}

actual fun FlowContent.onInput(onInput: (String, BrowserElement) -> Unit) {
    val elem = prepareElement()
    elem as GlobalEventHandlers

    elem.oninput = { e ->
        onInput(e.data, BrowserElement(elem))
    }
}

@OptIn(ExperimentalTime::class)
actual fun FlowContent.onDispose(onDispose: (BrowserElement) -> Unit) {
    val elem = prepareElement()
    elem as GlobalEventHandlers

    launch {
        try {
            delay(Duration.INFINITE)
        } catch (e: CancellationException) {
            onDispose(BrowserElement(elem))
        }
    }

//    builder.currentBuildContext.onDispose {
//        onDispose(BrowserElement(elem))
//    }
}

actual fun FlowContent.onScroll(onScroll: (BrowserElement) -> Unit) {
    val elem = prepareElement()
    elem as GlobalEventHandlers

    elem.onscroll = {
        onScroll(BrowserElement(elem))
    }
}

actual fun FlowContent.onKeyUp(onKeyUp: (String) -> Unit) {
    val elem = prepareElement()
    elem as GlobalEventHandlers

    elem.onkeyup = {
        onKeyUp(it.key)
    }
}