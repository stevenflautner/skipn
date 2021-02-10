package io.skipn.events

import io.skipn.browser.BrowserAny
import org.w3c.dom.events.Event

fun Event.toBrowserEvent() = BrowserEvent(this)

fun Any.toBrowserElement() = BrowserAny(this)

actual class BrowserEvent(val jsEvent: Event) {

    actual val type: String = jsEvent.type
    actual val target: BrowserAny? = jsEvent.target?.toBrowserElement()
    actual val currentTarget: BrowserAny? = jsEvent.currentTarget?.toBrowserElement()
    actual val eventPhase: Short = jsEvent.eventPhase
    actual val bubbles: Boolean = jsEvent.bubbles
    actual val cancelable: Boolean = jsEvent.cancelable
    actual val defaultPrevented: Boolean = jsEvent.defaultPrevented
    actual val composed: Boolean = jsEvent.composed
    actual val isTrusted: Boolean = jsEvent.isTrusted
    actual val timeStamp: Number = jsEvent.timeStamp
//    actual fun composedPath(): Array<BrowserElement> = jsEvent.composedPath()
    actual fun stopPropagation() = jsEvent.stopPropagation()
    actual fun stopImmediatePropagation() = jsEvent.stopImmediatePropagation()
    actual fun preventDefault() = jsEvent.preventDefault()

//    actual companion object {
//        actual val NONE: Short
//            get() = TODO("Not yet implemented")
//        actual val CAPTURING_PHASE: Short
//            get() = TODO("Not yet implemented")
//        actual val AT_TARGET: Short
//            get() = TODO("Not yet implemented")
//        actual val BUBBLING_PHASE: Short
//            get() = TODO("Not yet implemented")
//    }
}

