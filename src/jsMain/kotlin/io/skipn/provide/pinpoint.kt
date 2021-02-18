package io.skipn.provide

import io.skipn.builder.buildContext
import io.skipn.prepareElement
import kotlinx.html.FlowContent

//actual inline fun <reified V> FlowContent.pinpoint(statefulValue: StatefulValue<V>): StatefulValue<V> {
//    val id = prepareElement().id
//
//    var stateBucket = buildContext.states[id]
//
//    if (stateBucket == null) {
//        stateBucket = StateBucket()
//        buildContext.states[id] = stateBucket
//    }
//
//    stateBucket.add(statefulValue)
//    buildContext.statesByClasses[V::class] = statefulValue
//    return statefulValue
//}
//
//actual inline fun <reified T : Stateful> FlowContent.pinpoint(stateful: T): T {
//    val id = prepareElement().id
//
//    var stateBucket = buildContext.states[id]
//
//    if (stateBucket == null) {
//        stateBucket = StateBucket()
//        buildContext.states[id] = stateBucket
//    }
//
//    stateBucket.add(stateful)
//    buildContext.statesByClasses[T::class] = stateful
//    return stateful
//}