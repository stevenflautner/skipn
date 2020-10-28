package io.skipn.observers

import io.skipn.builder.builder
import io.skipn.events.onDispose
import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import io.skipn.prepareElement
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.create

@HtmlTagMarker
actual fun <V: Any?, T> FlowContent.divOf(stateful: StatefulValue<V>, node: DIV.(V) -> T) {
    div {
        var element = prepareElement()

        // Creates a copy of the states
        val context = builder.createContext(element.id)

        val listener : (V) -> Unit = { value ->
            context.disposeChildrenTree()

            val newElement = document.create(context).div {
                id = element.id

                node(value)
            }
            element.replaceWith(newElement)
            element = newElement
        }
        stateful.listeners.add(listener)

        onDispose {
            stateful.listeners.remove(listener)
        }

        this@divOf.builder.descendBuilder(context)

        node(stateful.getValue())
    }
}

@HtmlTagMarker
actual fun FlowContent.divOf(stateful: Stateful, node: DIV.() -> Unit) {
    div {
        var element = prepareElement()

        val context = builder.createContext(element.id)

        val listener : () -> Unit = {
            context.disposeChildrenTree()

            val newElement = document.create(context).div {
                id = element.id

                node()
            }
            element.replaceWith(newElement)
            element = newElement
        }
        stateful.listeners.add(listener)

        onDispose {
            stateful.listeners.remove(listener)
        }

        this@divOf.builder.descendBuilder(context)

        node()
    }
}

//@HtmlTagMarker
//actual fun <T : Any, D> FlowContent.Stateful1Builder(
//    state: StatefulBuilderValuePredicate1<T, D>,
//    node: DIV.(D) -> Unit
//) {
//    div {
//        var element = prepareElement()
//
//        // Creates a copy of the states
//        val context = BuildContext(HashMap(builder.buildContext.states), HashMap(builder.buildContext.statesByClasses))
//
//        state.stateful.listen { value ->
//            val newElement = document.create(context).div {
//                id = element.id
//
//                state.predicate(value)?.let {
//                    node(it)
//                }
//            }
//            element.replaceWith(newElement)
//            element = newElement
//        }
//
//        state.predicate(state.stateful.getValue())?.let {
//            node(it)
//        }
//    }
//}

@HtmlTagMarker
actual fun <T : Any, R: Any> FlowContent.divOf(
    builder: StatefulFilterBuilder<T, R>,
    node: DIV.(R) -> Unit
) {
    div {
        var element = prepareElement()

        // Creates a copy of the states
        val context = this@divOf.builder.createContext(element.id)

        val listener : (T) -> Unit = {
            val result = builder.runFilter()

            if (builder.conditionsPassed && result != null) {
                context.disposeChildrenTree()

                val newElement = document.create(context).div {
                    id = element.id

                    node(result)
                }
                element.replaceWith(newElement)
                element = newElement
            }
        }
        builder.stateful.listeners.add(listener)

        onDispose {
            builder.stateful.listeners.remove(listener)
        }

        this@divOf.builder.descendBuilder(context)

        val result = builder.runFilter()
        if (result != null)
            node(result)
    }
}