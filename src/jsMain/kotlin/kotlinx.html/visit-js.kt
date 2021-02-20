package kotlinx.html

import VNode
import addHook
import io.skipn.Snabbdom
import io.skipn.builder.BuildContext
import io.skipn.builder.buildContext
import io.skipn.builder.builder
import io.skipn.getUnderlyingHtmlElement
import io.skipn.prepareElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import snabbdom.buildVDom



actual fun <T : Tag> T.visitTag(block: T.() -> Unit) {
    // Creates VNode
    consumer.onTagStart(this)
    try {
        // The current build context, before possibly descending
        // Block could contain a dependsOn() invocation which
        // would result the BuildContext to be descended.
        val parentScope = buildContext.getCoroutineScope()

        // BuildContext might change after the invocation
        this.block()

//        var vNode = getUnderlyingHtmlElement()
//
//        vNode.addHook("insert") {
//            println("DID THIS GET CALLED??")
////        vNode = it as VNode
//        }
//
//        vNode.addHook("pre") {
//            println("DID THIS GET CALLED??1222")
////        vNode = it as VNode
//        }
//        vNode.addHook("init") {
//            println("DID THIS GET CALLED??12")
//        }

        setupRebuild(this, parentScope, block)


//        var vNode = prepareElement()
//        val parentScope = buildContext.getCoroutineScope()

        // Creates a new Build Context
        // as a copy of the current one
//        val context = builder.createContextAndDescend(vNode)

//        val new = buildVDom(context).div {
//            block()
//        }

    } catch (err: Throwable) {
        consumer.onTagError(this, err)
    } finally {
        consumer.onTagEnd(this)
    }
}

private fun <T : Tag> setupRebuild(tag: T, parentScope: CoroutineScope, block: T.() -> Unit) {
    if (tag is HTMLTag) {
        if (tag.dependenciesBuilt) return
        tag.dependenciesBuilt = true

        val deps = tag.dependencies
        if (deps != null) {
            collectChangesAndRebuild(tag, deps, parentScope, block)
        }
    }
}

private fun <T: HTMLTag> collectChangesAndRebuild(
    tag: T,
    deps: List<Flow<*>>,
    parentScope: CoroutineScope,
    block: T.() -> Unit
) {
    var vNode = tag.getUnderlyingHtmlElement()
    val context = tag.buildContext

    vNode.addHook("postpatch") { old, new ->
        println("DID THIS GET CALLED?hhea?")
        println(old)
        println(new)
//        println(new as VNode)
        vNode = new as VNode
    }

    deps.forEach { flow ->
        val oldValue = flow.valueOrNull()

        parentScope.launch {
            val drop = if (oldValue == flow.valueOrNull())
                (flow as? SharedFlow)?.replayCache?.size ?: 0
            else 0
            // Drop all values in the replay cache
            // So we only notify newly emitted values
            // TODO BY THE TIME WE DROP VALUES MIGHT HAVE CHANGED
            //  WE SHOULD DO AN EQUALITY CHECK
            flow.drop(drop).collectLatest {
                if (vNode.elm == null) {
                    vNode.addHook("insert") { _, _ ->
                        context.cancelAndCreateScope(parentScope)

                        context.launch {
                            val newVNode = buildVDom(context).let { newConsumer ->
                                tag.depId = 0
                                tag.consumer = newConsumer
                                tag.visitAndFinalize(newConsumer, block)
                            }
                            println("TESS1")
                            println(JSON.stringify(vNode))
                            println(JSON.stringify(newVNode))
                            vNode = Snabbdom.patch(vNode, newVNode)
                        }
                    }
                } else {
                    context.cancelAndCreateScope(parentScope)

                    context.launch {
                        val newVNode = buildVDom(context).let { newConsumer ->
                            tag.depId = 0
                            tag.consumer = newConsumer
                            tag.visitAndFinalize(newConsumer, block)
                        }
                        println("TESS")
                        println(vNode.elm?.parentElement)
                        println(vNode.elm?.parentNode)
                        println(vNode.elm?.parentNode == null)

                        // Its possible that the new vnode's child is not the one that was patched right.

                        println(JSON.stringify(vNode))
                        println(JSON.stringify(newVNode))
                        val parent = vNode.elm
                        vNode = Snabbdom.patch(vNode, newVNode).also { new ->
                            new.elm = parent
                        }

                    }
                }
            }
        }



//        listen(parentScope, flow, oldValue, context, tag, block, vNode)
//        if (vNode.elm == null) {
//            vNode.addHook("insert") {
//                listen(parentScope, flow, oldValue, context, tag, block, vNode)
//            }
//        }
//        else {
//            listen(parentScope, flow, oldValue, context, tag, block, vNode)
//        }

    }
}

//private fun <T : HTMLTag> listen(
//    parentScope: CoroutineScope,
//    flow: Flow<*>,
//    oldValue: Any?,
//    context: BuildContext,
//    tag: T,
//    block: T.() -> Unit,
//    vNode: VNode
//) {
//    var vNode1 = vNode
//
//    parentScope.launch {
//        val drop = if (oldValue == flow.valueOrNull())
//            (flow as? SharedFlow)?.replayCache?.size ?: 0
//        else 0
//        // Drop all values in the replay cache
//        // So we only notify newly emitted values
//        // TODO BY THE TIME WE DROP VALUES MIGHT HAVE CHANGED
//        //  WE SHOULD DO AN EQUALITY CHECK
//        flow.drop(drop).collectLatest {
//            if (vNode1.elm == null) {
//                vNode1.addHook("insert") { _, _ ->
//                    context.cancelAndCreateScope(parentScope)
//
//                    context.launch {
//                        val newVNode = buildVDom(context).let { newConsumer ->
//                            tag.depId = 0
//                            tag.consumer = newConsumer
//                            tag.visitAndFinalize(newConsumer, block)
//                        }
//                        println("TESS1")
//                        println(JSON.stringify(vNode1))
//                        println(JSON.stringify(newVNode))
//                        vNode1 = Snabbdom.patch(vNode1, newVNode)
//                    }
//                }
//            } else {
//                context.cancelAndCreateScope(parentScope)
//
//                context.launch {
//                    val newVNode = buildVDom(context).let { newConsumer ->
//                        tag.depId = 0
//                        tag.consumer = newConsumer
//                        tag.visitAndFinalize(newConsumer, block)
//                    }
//                    println("TESS")
//                    println(vNode1.elm?.parentElement)
//                    println(vNode1.elm?.parentNode)
//                    println(vNode1.elm?.parentNode == null)
//
//                    // Its possible that the new vnode's child is not the one that was patched right.
//
//                    println(JSON.stringify(vNode1))
//                    println(JSON.stringify(newVNode))
//                    vNode1 = Snabbdom.patch(vNode1, newVNode)
//                }
//            }
//        }
//    }
//}

private fun Flow<*>.valueOrNull(): Any? {
    return if (this is SharedFlow) {
        replayCache.firstOrNull()
    } else null
}

actual fun <T : Tag, R> T.visitTagAndFinalize(consumer: TagConsumer<R>, block: T.() -> Unit): R {
//    if (this.consumer !== consumer) {
//        throw IllegalArgumentException("Wrong exception")
//    }

    visitTag(block)
    return consumer.finalize()
}