package io.skipn.events

import io.skipn.browser.BrowserElement
import io.skipn.builder.builder
import io.skipn.skipnContext
import io.skipn.prepareElement
import io.skipn.form.FormState
import io.skipn.Endpoint
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.FORM
import kotlinx.html.FlowContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.getContextualOrDefault
import org.w3c.dom.GlobalEventHandlers
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.xhr.FormData
import org.w3c.fetch.*

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
actual fun FlowContent.onClick(ignoreChildren: Boolean, onClick: () -> Unit) {
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

actual inline fun <reified RESP: Any> FORM.submitHandler(
        endpoint: Endpoint<*, RESP>, formState: FormState,
        crossinline onSuccess: (RESP) -> Unit
) : () -> Unit {
    val form = prepareElement() as HTMLFormElement

    form.addEventListener("submit", {
        it.preventDefault()
    })

    val submit = {
        if (formState.validateAll()) {
            window.fetch(endpoint.route, init = RequestInit(
                method = "post",
                body = FormData(form)
            )).then { response ->
                if (response.status == 200.toShort()) {
                    response.json()
                } else
                    println("there was an error...${response.status}")
            }.then { json: dynamic ->
                // TODO Update once overload resolution ambiguity is resolved
                val data = Json.decodeFromString<RESP>(
                    Json.serializersModule.getContextualOrDefault(),
                    JSON.stringify(json)
                )
                onSuccess(data)
            }
        }

//        GlobalScope.launch {
////            val response = api.post<RESP>(endpoint.route) {
////                body = FormData(form)
////                body = MultiPartFormDataContent()
////            }
//        }
    }

    form.onsubmit = { e ->
        e.preventDefault()
        submit()
    }
    return submit
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

actual fun FlowContent.onDispose(onDispose: (BrowserElement) -> Unit) {
    val elem = prepareElement()
    elem as GlobalEventHandlers

    builder.currentBuildContext.onDispose {
        onDispose(BrowserElement(elem))
    }
}

actual fun FlowContent.onScroll(onScroll: (BrowserElement) -> Unit) {
    val elem = prepareElement()
    elem as GlobalEventHandlers

    elem.onscroll = {
        onScroll(BrowserElement(elem))
    }
}