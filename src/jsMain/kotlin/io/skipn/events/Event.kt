package io.skipn.events

import org.w3c.dom.events.Event

actual class Event(val jsEvent: Event) {

    actual fun preventDefault() {
        jsEvent.preventDefault()
    }

}