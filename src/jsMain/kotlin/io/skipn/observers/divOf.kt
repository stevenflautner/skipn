package io.skipn.observers

import io.skipn.builder.BuildContext
import io.skipn.builder.buildContext
import io.skipn.builder.builder
import io.skipn.html.create
import io.skipn.prepareElement
import kotlinx.browser.document
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.html.*
import morphdom.MorphDomOptions
import morphdom.morphdom
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

@HtmlTagMarker
actual fun <V, T> FlowContent.divOf(stateFlow: StateFlow<V>, node: DIV.(V) -> T) {
    div {
        var element = prepareElement()
        val parentScope = buildContext.getCoroutineScope()

        // Creates a new Build Context
        // as a copy of the current one
        val context = builder.createContextAndDescend(element.id)

        val currentValue = stateFlow.value

        // Run node first
        node(currentValue)

        // Listen for changes and ignore
        // first value emitted
        parentScope.launch {
            // It's possible that by the time this coroutine runs,
            // the current value of the stateflow changed.
            // Then don't drop the first value
            stateFlow.drop(stateFlow.dropCount(currentValue)).collect { value ->
                context.cancelAndCreateScope(parentScope)
                context.getCoroutineScope().launch {
                    element = replaceElement(element, context) {
                        node(value)
                    }
                }
            }
        }
    }
}

@HtmlTagMarker
actual fun FlowContent.divOf(
    flow: Flow<*>,
    node: DIV.() -> Unit
) {
    div {
        var element = prepareElement()
        val parentScope = buildContext.getCoroutineScope()

        // Creates a new Build Context
        // as a copy of the current one
        val context = builder.createContextAndDescend(element.id)

        val oldValue = flow.valueOrNull()

        // Run node first
        node()

        // Listen for changes and ignore
        // first value emitted
        parentScope.launch {
            // Drop all values in the replay cache
            // So we only notify newly emitted values
            flow.drop(flow.dropCount(oldValue)).collect {
                context.cancelAndCreateScope(parentScope)
                context.getCoroutineScope().launch {
                    element = replaceElement(element, context) {
                        node()
                    }
                }
            }
        }
    }
}

//val d = io.skipn.utils.require_("morphdom.morphdom")
//val morphdom = js("require('morphdom')")

fun replaceElement(element: Element, context: BuildContext, node: DIV.() -> Unit): Element {
    val newElement = document.create(context).div {
        id = element.id

        node()
    }
//    println("DOMM")
//    println(element.classList)
//    println(element.classList.value)
//    println("DOMM1")
//    println(newElement.classList)
//    println(newElement.classList.value)
//    d(element, newElement)
//    return morphdom(element, newElement, options = object : MorphDomOptions {
//        override var childrenOnly: Boolean? = false
//        override var onBeforeElUpdated: ((fromEl: HTMLElement, toEl: HTMLElement) -> Boolean)? = { fromEl, toEl ->
//            // spec - https://dom.spec.whatwg.org/#concept-node-equals
//            !fromEl.isEqualNode(toEl)
//        }
//    }) as Element


    element.replaceWith(newElement)
    return newElement
//    return morphdom(element, newElement) as? Element ?: throw Exception("MORPHDOM FAILED TO RETURN ELEMENT")
}

//fun replaceElement(element: Element, context: BuildContext, node: DIV.() -> Unit): Element {
//    val newElement = document.create(context).div {
//        id = element.id
//
//        node()
//    }
//    element.replaceWith(newElement)
//    return newElement
//}

@HtmlTagMarker
actual fun <T> FlowContent.divOf(flow: Flow<T>, initialValue: T, node: DIV.(T) -> Unit) {
    divOf(flow.stateIn(buildContext.getCoroutineScope(), SharingStarted.Eagerly, initialValue), node)
}