package io.skipn.html

import io.skipn.builder.BuildContext
import io.skipn.builder.Builder
import kotlinx.html.*
import kotlinx.html.consumers.delayed
import kotlinx.html.consumers.onFinalizeMap
import org.w3c.dom.events.Event

class SkipnInitializationBuilder(
        override val rootBuildContext: BuildContext,
) : TagConsumer<Unit>, Builder {

    override val builderContextTree: ArrayDeque<BuildContext> = ArrayDeque(1)
    override var currentBuildContext: BuildContext? = rootBuildContext

    init {
        builderContextTree.addFirst(rootBuildContext)
    }

    override fun getBuildContext(): BuildContext = currentBuildContext ?: throw IllegalStateException("Build Context was null")

    override fun onTagStart(tag: Tag) {}

    override fun onTagEnd(tag: Tag) {
        val id = tag.attributes["id"]

        if (id != null) {
            ascend(id)
        }
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        throw UnsupportedOperationException("tag attribute can't be changed as it was already written to the stream. Use with DelayedConsumer to be able to modify attributes")
    }
    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
        throw UnsupportedOperationException("you can't assign lambda event handler when building text")
    }
    override fun onTagContent(content: CharSequence) {}
    override fun onTagContentEntity(entity: Entities) {}
    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {}
    override fun onTagComment(content: CharSequence) {}

    override fun finalize() {}
}

fun createHTML(rootBuildContext: BuildContext): TagConsumer<Unit> =
        SkipnInitializationBuilder(
            rootBuildContext,
        ).delayed()