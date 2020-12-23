package io.skipn.html

import io.skipn.builder.BuildContext
import io.skipn.builder.Builder
import kotlinx.html.Entities
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.Unsafe
import org.w3c.dom.events.Event

//class StreamBuilder(
//        val out: O,
//        override val rootBuildContext: BuildContext,
//        val prettyPrint: Boolean,
//        val xhtmlCompatible: Boolean
//) :
//        TagConsumer<O>, Builder {
//    private var level = 0
//    private var ln = true
//
////    val skipnContext = SkipnContext()
//
//    var routeLevel: Int = 0
//    var lastRouterId
//
//    override val builderContextTree: ArrayDeque<BuildContext> = ArrayDeque(1)
//    override var currentBuildContext: BuildContext = rootBuildContext
//
//    init {
//
//        builderContextTree.addFirst(rootBuildContext)
//    }
//
//    override fun onTagStart(tag: Tag) {
//        if (prettyPrint && !tag.inlineTag) {
//            indent()
//        }
//        level++
//
//        out.append("<")
//        out.append(tag.tagName)
//
//        if (tag.namespace != null) {
//            out.append(" xmlns=\"")
//            out.append(tag.namespace)
//            out.append("\"")
//        }
//
//        if (tag.attributes.isNotEmpty()) {
//            tag.attributesEntries.forEachIndexed { _, e ->
//                if (!e.key.isValidXmlAttributeName()) {
//                    throw IllegalArgumentException("Tag ${tag.tagName} has invalid attribute name ${e.key}")
//                }
//
//                out.append(' ')
//                out.append(e.key)
//                out.append("=\"")
//                out.escapeAppend(e.value)
//                out.append('\"')
//            }
//        }
//
//        if (xhtmlCompatible && tag.emptyTag) {
//            out.append("/")
//        }
//
//        out.append(">")
//        ln = false
//    }
//
//    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
//        throw UnsupportedOperationException("tag attribute can't be changed as it was already written to the stream. Use with DelayedConsumer to be able to modify attributes")
//    }
//
//    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
//        throw UnsupportedOperationException("you can't assign lambda event handler when building text")
//    }
//
//    override fun onTagEnd(tag: Tag) {
//        val id = tag.attributes["id"]
//
//        if (id != null) {
//            ascend(id)
//        }
//
//        level--
//        if (ln) {
//            indent()
//        }
//
//        if (!tag.emptyTag) {
//            out.append("</")
//            out.append(tag.tagName)
//            out.append(">")
//        }
//
//        if (prettyPrint && !tag.inlineTag) {
//            appendln()
//        }
//    }
//
//    override fun onTagContent(content: CharSequence) {
//        out.escapeAppend(content)
//        ln = false
//    }
//
//    override fun onTagContentEntity(entity: Entities) {
//        out.append(entity.text)
//        ln = false
//    }
//
//    override fun finalize(): O = out
//
//    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
//        UnsafeImpl.block()
//    }
//
//    override fun onTagComment(content: CharSequence) {
//        if (prettyPrint) {
//            indent()
//        }
//
//        out.append("<!--")
//        out.escapeComment(content)
//        out.append("-->")
//
//        ln = false
//    }
//
//    val UnsafeImpl = object : Unsafe {
//        override operator fun String.unaryPlus() {
//            out.append(this)
//        }
//    }
//
//    private fun appendln() {
//        if (prettyPrint && !ln) {
//            out.append("\n")
//            ln = true
//        }
//    }
//
//    private fun indent() {
//        if (prettyPrint) {
//            if (!ln) {
//                out.append("\n")
//            }
//            var remaining = level
//            while (remaining >= 4) {
//                out.append("        ")
//                remaining -= 4
//            }
//            while (remaining >= 2) {
//                out.append("    ")
//                remaining -= 2
//            }
//            if (remaining > 0) {
//                out.append("  ")
//            }
//            ln = false
//        }
//    }
//}