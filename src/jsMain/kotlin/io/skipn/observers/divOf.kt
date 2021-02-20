//package io.skipn.observers
//
//import VNode
//import io.skipn.Snabbdom
//import io.skipn.builder.BuildContext
//import io.skipn.builder.buildContext
//import io.skipn.builder.builder
//import io.skipn.prepareElement
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import kotlinx.html.*
//import snabbdom.buildVDom
//
//@HtmlTagMarker
//actual fun <V, T> FlowContent.divOf(stateFlow: StateFlow<V>, node: DIV.(V) -> T) {
//    div {
//        var vNode = prepareElement()
//        val parentScope = buildContext.getCoroutineScope()
//
//        // Creates a new Build Context
//        // as a copy of the current one
//        val context = builder.createContextAndDescend(vNode)
//
//        val currentValue = stateFlow.value
//
//        // Run node first
//        node(currentValue)
//
//        // Listen for changes and ignore
//        // first value emitted
//        parentScope.launch {
//            // It's possible that by the time this coroutine runs,
//            // the current value of the stateflow changed.
//            // Then don't drop the first value
//            val drop = if (currentValue == stateFlow.value) 1 else 0
//
//            stateFlow.drop(drop).collect { value ->
//                context.cancelAndCreateScope(parentScope)
//
//                vNode = replaceElement(vNode, context) {
//                    node(value)
//                }
//
////                context.getCoroutineScope().launch {
////                    vNode = replaceElement(vNode, context) {
////                        node(value)
////                    }
////                }
//            }
//        }
//    }
//}
//
//@HtmlTagMarker
//actual fun FlowContent.divOf(
//    flow: Flow<*>,
//    node: DIV.() -> Unit
//) {
//    div {
//        var vNode = prepareElement()
//        val parentScope = buildContext.getCoroutineScope()
//
//        // Creates a new Build Context
//        // as a copy of the current one
//        val context = builder.createContextAndDescend(vNode)
//
//        // Run node first
//        node()
//
//        // Listen for changes and ignore
//        // first value emitted
//        parentScope.launch {
//            val drop = (flow as? SharedFlow)?.replayCache?.size ?: 0
//            // Drop all values in the replay cache
//            // So we only notify newly emitted values
//            flow.drop(drop).collect {
//                context.cancelAndCreateScope(parentScope)
//
//                vNode = replaceElement(vNode, context) {
//                    node()
//                }
//
////                context.getCoroutineScope().launch {
////                    vNode = replaceElement(vNode, context) {
////                        node()
////                    }
////                }
//            }
//        }
//    }
//}
//
////val d = io.skipn.utils.require_("morphdom.morphdom")
////val morphdom = js("require('morphdom')")
//
//fun replaceElement(old: VNode, context: BuildContext, node: DIV.() -> Unit): VNode {
//    val new = buildVDom(context).div {
//        node()
//    }
//    return Snabbdom.patch(old, new)
//
//
//
//
////    println("DOMM")
////    println(element.classList)
////    println(element.classList.value)
////    println("DOMM1")
////    println(newElement.classList)
////    println(newElement.classList.value)
////    d(element, newElement)
////    morphdom(element, newElement, options = object : MorphDomOptions {
////        override var childrenOnly: Boolean? = false
//////        override var onBeforeElUpdated: ((fromEl: HTMLElement, toEl: HTMLElement) -> Boolean)? = { fromEl, toEl ->
//////            // spec - https://dom.spec.whatwg.org/#concept-node-equals
//////            !fromEl.isEqualNode(toEl)
//////        }
////    })
//
//
////    element.replaceWith(newElement)
////    return newElement
////    return morphdom(element, newElement) as? Element ?: throw Exception("MORPHDOM FAILED TO RETURN ELEMENT")
//}
//
////fun replaceElement(element: Element, context: BuildContext, node: DIV.() -> Unit): Element {
////    val newElement = document.create(context).div {
////        id = element.id
////
////        node()
////    }
////    element.replaceWith(newElement)
////    return newElement
////}
//
//@HtmlTagMarker
//actual fun <T> FlowContent.divOf(flow: Flow<T>, initialValue: T, node: DIV.(T) -> Unit) {
//    divOf(flow.stateIn(buildContext.getCoroutineScope(), SharingStarted.Eagerly, initialValue), node)
//}