package io.skipn.provide

import kotlinx.coroutines.flow.StateFlow
import kotlinx.html.Tag
import kotlin.reflect.KClass

typealias PinMap = HashMap<KClass<*>, Any?>

class PinBucket(
    val tag: Tag,
) {
    val pins = PinMap()
}

// Holds all the references for the pins required
// when rebuilding in the browser
class PinningContext(parent: PinningContext?) {

    val children by lazy {
        hasPinned = true
        ArrayDeque<PinBucket>()
    }

//     Caches the next element to be disposed
//    private var nextToDispose : PinBucket? = null

    // The base corresponds to the provided instances up until
    // this PinBucket, that the parent PinBucket collected within
    // this node. It is required so that we can rebuild and still
    // hold every dependency that our child nodes may require
    val base: PinMap

    // Copy the parent's pins
    init {
        base = parent?.pack() ?: PinMap()
    }

    // Defines whether there was any pin added
    // within this PinBucket
    var hasPinned = false
        private set

    // Packs current pinning context Into a hashmap.
    // Creating a pack is useful to access and store
    // all the references up until the invocation of pack
    private fun pack(): PinMap {
        return PinMap(base).apply {
            if (hasPinned) {
                children.forEach { bucket ->
                    putAll(bucket.pins)
                }
            }
        }
    }

    // Retrieves the last bucket and if it's not for the current
    // elem, adds a new elem at the end of the queue
    fun getBucket(tag: Tag): PinBucket {
        val last = children.lastOrNull()

        return if (last == null) {
            PinBucket(tag).also {
                children.addLast(it)
            }
        } else {
            if (last.tag == tag)
                last
            else {
                PinBucket(tag).also {
                    children.addLast(it)
                }
            }
        }
    }

    inline fun <reified T: Any?> pinInstance(tag: Tag, instance: T) {
        val bucket = getBucket(tag)
        bucket.pins[T::class] = instance
    }

    inline fun <reified V: Any?, T: StateFlow<V>> pinStateFlow(tag: Tag, stateFlow: T) {
        val bucket = getBucket(tag)
        bucket.pins[V::class] = stateFlow
    }

    // Should always call ascend when any tag finished
    // within the current context
    fun ascend(tag: Tag) {
        val last = children.lastOrNull() ?: return
        // Found element to dispose
        if (last.tag === tag) {
            children.removeLast()
        }
    }

    // First iterate through the children queue trying to find the instance
    // If not found it uses the base map to look for the instance
    // otherwise returns null if not found anywhere
    inline fun <reified T: Any?> findInstanceOrNull(): Any? {
        if (children.isEmpty()) {
            return base[T::class]
        }
        else {
            children.findLast {
                val instance = it.pins[T::class]
                if (instance == null) false
                else {
                    return instance
                }
            }
            return base[T::class]
        }
    }
}