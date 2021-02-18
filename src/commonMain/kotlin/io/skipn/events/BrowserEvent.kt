package io.skipn.events

import io.skipn.browser.BrowserAny

expect class BrowserEvent {

    val type: String
    val target: BrowserAny?
    val currentTarget: BrowserAny?
    val eventPhase: Short
    val bubbles: Boolean
    val cancelable: Boolean
    val defaultPrevented: Boolean
    val composed: Boolean
    val isTrusted: Boolean
    val timeStamp: Number
//    fun composedPath(): Array<BrowserElement>
    fun stopPropagation()
    fun stopImmediatePropagation()
    fun preventDefault()

//    companion object {
//        val NONE: Short
//        val CAPTURING_PHASE: Short
//        val AT_TARGET: Short
//        val BUBBLING_PHASE: Short
//    }
}

//public external interface EventInit {
//    var bubbles: Boolean? /* = false */
//        get() = definedExternally
//        set(value) = definedExternally
//    var cancelable: Boolean? /* = false */
//        get() = definedExternally
//        set(value) = definedExternally
//    var composed: Boolean? /* = false */
//        get() = definedExternally
//        set(value) = definedExternally
//}

//public external open class Event(type: String, eventInitDict: EventInit = definedExternally) {
//    open val type: String
//    open val target: EventTarget?
//    open val currentTarget: EventTarget?
//    open val eventPhase: Short
//    open val bubbles: Boolean
//    open val cancelable: Boolean
//    open val defaultPrevented: Boolean
//    open val composed: Boolean
//    open val isTrusted: Boolean
//    open val timeStamp: Number
//    fun composedPath(): Array<EventTarget>
//    fun stopPropagation()
//    fun stopImmediatePropagation()
//    fun preventDefault()
//    fun initEvent(type: String, bubbles: Boolean, cancelable: Boolean)
//
//    companion object {
//        val NONE: Short
//        val CAPTURING_PHASE: Short
//        val AT_TARGET: Short
//        val BUBBLING_PHASE: Short
//    }
//}

//public external abstract class EventTarget {
//    fun addEventListener(type: String, callback: EventListener?, options: dynamic = definedExternally)
//    fun addEventListener(type: String, callback: ((Event) -> Unit)?, options: dynamic = definedExternally)
//    fun removeEventListener(type: String, callback: EventListener?, options: dynamic = definedExternally)
//    fun removeEventListener(type: String, callback: ((Event) -> Unit)?, options: dynamic = definedExternally)
//    fun dispatchEvent(event: Event): Boolean
//}