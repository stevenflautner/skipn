package io.skipn.events

import VNode
import addHook
import io.skipn.FormEndpoint
import io.skipn.browser.BrowserElement
import io.skipn.builder.launch
import io.skipn.form.FormBuilder
import io.skipn.getUnderlyingHtmlElement
import io.skipn.prepareElement
import io.skipn.skipnContext
import kotlinx.browser.document
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.FORM
import kotlinx.html.FlowContent
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.xhr.FormData
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

//@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
//fun CommonAttributeGroupFacade.onClick(onClick: ((BrowserEvent) -> Unit)) = onEvent("click", onClick)

actual inline fun FlowContent.onEvent(name: String, crossinline onEvent: ((BrowserEvent) -> Unit)) {
    consumer.onTagEvent(this, name) {
        onEvent(BrowserEvent(it))
    }
}

inline fun FlowContent.internalOnEvent(name: String, crossinline onEvent: ((Event) -> Unit)) {
    consumer.onTagEvent(this, name) {
        onEvent(it)
    }
}

actual fun FlowContent.onMounted(onMounted: (BrowserElement) -> Unit) {
//    val element = prepareElement()
    val vNode = getUnderlyingHtmlElement()

    vNode.addHook("insert") {
        onMounted(BrowserElement(it.elm as Element))
    }

//    if (skipnContext.isInitializing) {
//        onMounted(BrowserElement(element))
//    } else {
//        MutationObserver { records, observer ->
//            if (document.contains(element)) {
//                onMounted(BrowserElement(element))
//                observer.disconnect()
//            }
//        }.observe(document, MutationObserverInit(subtree = true, childList = true))
//    }
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual fun FlowContent.onClick(ignoreChildren: Boolean, onClick: ((BrowserEvent) -> Unit)?) {
    if (onClick == null) return
//    val vNode = getUnderlyingHtmlElement()

    onEvent("click") {
//        if (it.target != elem && ignoreChildren) null
//        else onClick(BrowserEvent(it))
        onClick(it)
    }

//    elem.onclick = {
//        if (it.target != elem && ignoreChildren) null
//        else onClick(BrowserEvent(it))
//    }
}

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
actual fun FlowContent.onHover(onHover: (Boolean) -> Unit) {
//    val elem = prepareElement() as GlobalEventHandlers
    var hovering = false

    onEvent("mouseenter") {
        if (!hovering) {
            hovering = true
            onHover(hovering)
        }
    }
    onEvent("mouseleave") {
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
    val vNode = prepareElement()

    internalOnEvent("input") {
        onInput(it.asDynamic().data as String, BrowserElement(vNode.elm as Element))
    }
//    val elem = prepareElement()
//    elem as GlobalEventHandlers
//
//    elem.oninput = { e ->
//        onInput(e.data, BrowserElement(elem))
//    }
}

@OptIn(ExperimentalTime::class)
actual fun FlowContent.onDispose(onDispose: (BrowserElement) -> Unit) {
    val vNode = prepareElement()
//    elem as GlobalEventHandlers

    vNode.addHook("destroy") {
        onDispose(BrowserElement(it.elm as Element))
    }

//    launch {
//        try {
//            delay(Duration.INFINITE)
//        } catch (e: CancellationException) {
//            onDispose(BrowserElement(elem.elm as Element))
//        }
//    }

//    builder.currentBuildContext.onDispose {
//        onDispose(BrowserElement(elem))
//    }
}

actual fun FlowContent.onScroll(onScroll: (BrowserElement) -> Unit) {
    val vNode = prepareElement()

    onEvent("scroll") {
        onScroll(BrowserElement(vNode.elm as Element))
    }
//    onScroll
//    val elem = prepareElement()
//    elem as GlobalEventHandlers
//
//    elem.onscroll = {
//        onScroll(BrowserElement(elem))
//    }
}

actual fun FlowContent.onKeyUp(onKeyUp: (String) -> Unit) {
    internalOnEvent("keyup") {
        onKeyUp((it as KeyboardEvent).key)
    }
//    val elem = prepareElement()
//    elem as GlobalEventHandlers
//
//    elem.onkeyup = {
//        onKeyUp(it.key)
//    }
}

actual fun FlowContent.onChange(onChange: (BrowserEvent) -> Unit) {
    onEvent("change", onChange)
//    prepareEvent().onchange = {
//        onChange(it.toBrowserEvent())
//    }
}

private fun FlowContent.prepareEvent() = prepareElement()
    .let { it as GlobalEventHandlers }