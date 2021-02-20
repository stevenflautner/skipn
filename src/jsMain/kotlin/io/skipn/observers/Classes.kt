package io.skipn.observers

import VNode
import addAttr
import io.skipn.builder.buildContext
import io.skipn.builder.launch
import io.skipn.prepareElement
import kotlinx.coroutines.flow.*
import kotlinx.html.FlowContent
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement

private fun updateElement(vNode: VNode, name: String, value: String) {
//    vNode.addAttr(name, value)
//    when (element) {
//        is HTMLInputElement -> {
//            when(name) {
//                "value" -> element.value = value
//                "checked" -> {
//                    element.checked = value == "true"
//                }
//                else -> element.setAttribute(name, value)
//            }
//        }
//        else -> element.setAttribute(name, value)
//    }
}

//actual fun <T> FlowContent.attributeOf(
//    name: String,
//    stateFlow: StateFlow<T>,
//    value: (T) -> String
//) {
//    val vNode = prepareElement()
//
//    updateElement(vNode, name, value(stateFlow.value))
//
//    launch {
//        stateFlow.drop(1).collect {
//            // TODO CHANGE THIS TO VALUEOF
//            updateElement(vNode, name, value(stateFlow.value))
//        }
//    }
//}
//
//actual fun <T> FlowContent.attributeOf(
//    name: String,
//    flow: Flow<T>,
//    value: () -> String
//) {
//    val element = prepareElement()
//
//    // TODO CHANGE THIS TO VALUEOF
//    updateElement(element, name, value())
//
//    launch {
//        val drop = (flow as? SharedFlow)?.replayCache?.size ?: 0
//        // Drop all values in the replay cache
//        // So we only notify newly emitted values
//        flow.drop(drop).collect {
//            updateElement(element, name, value())
//        }
//    }
//}
//
//actual fun <T> FlowContent.attributeOf(name: String, flow: Flow<T>, initialValue: T, value: (T) -> String) {
//    attributeOf(name, flow.stateIn(buildContext.getCoroutineScope(), SharingStarted.Eagerly, initialValue), value)
//}