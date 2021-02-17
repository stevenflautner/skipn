//package io.skipn.provide
//
//import kotlinx.coroutines.flow.StateFlow
//import kotlin.reflect.KClass
//
///**
// * On the server there's only one PinningContext alive,
// * therefore we don't need a traversing tree of dependencies,
// * we can store a set, and simply override with new values
// */
//actual class PinningContext actual constructor(parent: PinningContext?) {
//
//    val pins = mutableSetOf<KClass<*>>()
//
//    inline fun <reified T: Any?> pinInstance(vNode: VNode, instance: T) {
//        val bucket = getBucket(vNode)
//        bucket.pins[T::class] = instance
//    }
//
//    inline fun <reified V: Any?, T: StateFlow<V>> pinStateFlow(vNode: VNode, stateFlow: T) {
//        val bucket = getBucket(vNode)
//        bucket.pins[V::class] = stateFlow
//    }
//
//    // First iterate through the children queue trying to find the instance
//    // If not found it uses the base map to look for the instance
//    // otherwise returns null if not found anywhere
//    inline fun <reified T: Any?> findInstanceOrNull(): Any? {
//        if (children.isEmpty()) {
//            return base[T::class]
//        }
//        else {
//            children.findLast {
//                val instance = it.pins[T::class]
//                if (instance == null) false
//                else {
//                    return instance
//                }
//            }
//            return base[T::class]
//        }
//    }
//}