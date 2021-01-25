package io.skipn.observers

import io.skipn.state.StatefulValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HtmlTagMarker

class ClassesBuilder {

    private var classes: String = ""

    operator fun String.unaryPlus() {
        classes += " $this"
    }

    fun build(): String {
        return classes
    }
}

private const val CLASS_ATTR = "class"

fun <T> FlowContent.classesOf(statefulValue: StatefulValue<T>, classes: ClassesBuilder.(T) -> Unit) {
    attributeOf(CLASS_ATTR, statefulValue) { newValue ->
        buildClasses(classes, newValue)
    }
}

//fun <T: Any?> FlowContent.classesOf(stateFlow: StateFlow<T>, classes: ClassesBuilder.(T) -> Unit) {
//    attributeOf("class", stateFlow) { value ->
//        buildClasses(classes, value)
//    }
//}
//fun <T: Any?> FlowContent.classesOf(flow: Flow<T>, classes: ClassesBuilder.() -> Unit) {
//    attributeOf("class", flow) {
//        buildClasses(classes)
//    }
//}
//fun <T: Any?> FlowContent.classesOf(flow: Flow<T>, initialValue: T, classes: ClassesBuilder.(T) -> Unit) {
//    attributeOf("class", flow, initialValue) {
//        buildClasses(classes, it)
//    }
//}
//fun <T: Any?, R: Any?> FlowContent.classesOf(stateFlowDef: StateFlowDef<T, R>, classes: ClassesBuilder.(R) -> Unit) {
//    attributeOf("class", stateFlowDef) {
//        buildClasses(classes, it)
//    }
//}

expect fun <T> FlowContent.attributeOf(name: String, statefulValue: StatefulValue<T>, value: (T) -> String)
//expect fun <T: Any?> FlowContent.attributeOf(name: String, stateFlow: StateFlow<T>, value: (T) -> String)
//expect fun <T: Any?> FlowContent.attributeOf(name: String, flow: Flow<T>, value: () -> String)
//expect fun <T: Any?> FlowContent.attributeOf(name: String, flow: Flow<T>, initialValue: T, value: (T) -> String)
//fun <T: Any?, R: Any?> FlowContent.attributeOf(name: String, stateFlowDef: StateFlowDef<T, R>, value: (R) -> String) {
//    with(stateFlowDef) {
//        attributeOf(name, parentStateFlow.map { transform(it) }, initialValue = stateFlowDef.value, value)
//    }
//}

fun buildClasses(classes: ClassesBuilder.() -> Unit) = ClassesBuilder().apply(classes).build()
fun <T: Any?> buildClasses(classes: ClassesBuilder.(T) -> Unit, value: T) = ClassesBuilder().apply {
    classes(value)
}.build()