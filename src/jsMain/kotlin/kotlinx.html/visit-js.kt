package kotlinx.html

import io.skipn.Snabbdom
import io.skipn.builder.buildContext
import io.skipn.builder.builder
import io.skipn.getUnderlyingHtmlElement
import io.skipn.prepareElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
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

        println("ASD111")
        println(parentScope === buildContext.getCoroutineScope())

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

    deps.forEach { flow ->
        parentScope.launch {
            val drop = (flow as? SharedFlow)?.replayCache?.size ?: 0
            // Drop all values in the replay cache
            // So we only notify newly emitted values
            flow.drop(drop).collect {
                context.cancelAndCreateScope(parentScope)

                val newVNode = buildVDom(context).let { newConsumer ->
                    tag.depId = 0
                    tag.consumer = newConsumer
                    tag.visitAndFinalize(newConsumer, block)
                }
                vNode = Snabbdom.patch(vNode, newVNode)
            }
        }
    }
}

actual fun <T : Tag, R> T.visitTagAndFinalize(consumer: TagConsumer<R>, block: T.() -> Unit): R {
//    if (this.consumer !== consumer) {
//        throw IllegalArgumentException("Wrong exception")
//    }

    visitTag(block)
    return consumer.finalize()
}