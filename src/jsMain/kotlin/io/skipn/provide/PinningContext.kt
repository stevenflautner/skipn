//package io.skipn.provide
//
//import VNode
//import kotlinx.coroutines.flow.StateFlow
//import kotlin.reflect.KClass
//
//typealias PinMap = HashMap<KClass<*>, Any?>
//
//class PinBucket(
//    val vNode: VNode,
//) {
//    val pins = PinMap()
//}
//
//actual class PinningContext actual constructor(parent: PinningContext?) {
//
//    val children by lazy {
//        hasPinned = true
//        ArrayDeque<PinBucket>()
//    }
//
////     Caches the next element to be disposed
////    private var nextToDispose : PinBucket? = null
//
//    // The base corresponds to the provided instances up until
//    // this PinBucket, that the parent PinBucket collected within
//    // this node. It is required so that we can rebuild and still
//    // hold every dependency that our child nodes may require
//    val base: PinMap
//
//    // Copy the parent's pins
//    init {
//        base = parent?.pack() ?: PinMap()
//    }
//
//    // Defines whether there was any pin added
//    // within this PinBucket
//    var hasPinned = false
//        private set
//
//    // Packs current pinning context Into a hashmap.
//    // Creating a pack is useful to access and store
//    // all the references up until the invocation of pack
//    protected fun pack(): PinMap {
//        return PinMap(base).apply {
//            if (hasPinned) {
//                children.forEach { bucket ->
//                    putAll(bucket.pins)
//                }
//            }
//        }
//    }
//
//    // Retrieves the last bucket and if it's not for the current
//    // elem, adds a new elem at the end of the queue
//    fun getBucket(vNode: VNode): PinBucket {
//        val last = children.lastOrNull()
//
//        return if (last == null) {
//            PinBucket(vNode).also {
//                children.addLast(it)
//            }
//        } else {
//            if (last.vNode === vNode)
//                last
//            else {
//                PinBucket(vNode).also {
//                    children.addLast(it)
//                }
//            }
//        }
//    }
//
//
//    // Should always call ascend when any tag finished
//    // within the current context
//    fun ascend(vNode: VNode) {
//        val last = children.lastOrNull() ?: return
//        // Found element to dispose
//        if (last.vNode === vNode) {
//            children.removeLast()
//        }
//    }
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