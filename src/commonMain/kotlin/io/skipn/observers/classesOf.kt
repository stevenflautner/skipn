package io.skipn.observers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.FlowContent

class ClassesBuilder {

    private var classes: String = ""

    operator fun String.unaryPlus() {
        classes += " $this"
    }

    fun build(): String {
        return classes
    }
}

expect fun <V: Any?> FlowContent.classesOf(stateFlow: StateFlow<V>, classes: ClassesBuilder.(V) -> Unit)
expect fun <V: Any?> FlowContent.classesOf(flow: Flow<V>, classes: ClassesBuilder.() -> Unit)
//expect fun FlowContent.classes(state: Stateful, classes: () -> String)

expect fun <T: Any?> FlowContent.attributeOf(name: String, stateFlow: StateFlow<T>, value: (T) -> String)

expect fun <T: Any?> FlowContent.attributeOf(name: String, flow: Flow<T>, value: () -> String)
//expect fun FlowContent.attribute(name: String, stateful: Stateful, value: () -> String)
//expect fun <T: Any, R: Any> FlowContent.attribute(name: String, builder: StatefulFilterBuilder<T, R>, value: (R) -> String)