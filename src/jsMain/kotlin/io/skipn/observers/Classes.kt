package io.skipn.observers

import io.skipn.builder.buildContext
import io.skipn.builder.launch
import io.skipn.prepareElement
import kotlinx.coroutines.flow.*
import kotlinx.html.FlowContent
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

private fun updateElement(element: Element, name: String, value: String) {
    when (element) {
        is HTMLInputElement -> {
            when(name) {
                "value" -> element.value = value
                "checked" -> {
                    element.checked = value == "true"
                }
                else -> element.setAttribute(name, value)
            }
        }
        else -> element.setAttribute(name, value)
    }
}

actual fun <T> FlowContent.attributeOf(
    name: String,
    stateFlow: StateFlow<T>,
    value: (T) -> String
) {
    val element = prepareElement()

    updateElement(element, name, value(stateFlow.value))

    val oldValue = stateFlow.valueOrNull()
    launch {
        stateFlow.drop(stateFlow.dropCount(oldValue)).collect {
            // TODO CHANGE THIS TO VALUEOF
            updateElement(element, name, value(stateFlow.value))
        }
    }
}

fun Flow<*>.valueOrNull(): Any? {
    return if (this is SharedFlow) {
        replayCache.firstOrNull()
    } else null
}

fun <T> Flow<T>.dropCount(oldValue: T): Int {
    return if (oldValue == valueOrNull())
        (this as? SharedFlow)?.replayCache?.size ?: 0
    else 0
}

actual fun <T> FlowContent.attributeOf(
    name: String,
    flow: Flow<T>,
    value: () -> String
) {
    val element = prepareElement()

    // TODO CHANGE THIS TO VALUEOF
    updateElement(element, name, value())

    val oldValue = flow.valueOrNull()

    launch {
        flow.drop(flow.dropCount(oldValue)).collect {
            updateElement(element, name, value())
        }
    }
}

actual fun <T> FlowContent.attributeOf(name: String, flow: Flow<T>, initialValue: T, value: (T) -> String) {
    attributeOf(name, flow.stateIn(buildContext.getCoroutineScope(), SharingStarted.Eagerly, initialValue), value)
}