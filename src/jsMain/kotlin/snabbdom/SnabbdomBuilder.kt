package snabbdom

import VNode
import addAttr
import addChild
import addClass
import io.skipn.builder.BuildContext
import io.skipn.builder.Builder
import kotlinx.html.*
import org.w3c.dom.events.Event
import removeAttr
import removeClasses
import setEvent

private const val CLASS_ATTR = "class"

class SnabbdomBuilder<out R : VNode>(
    override val rootBuildContext: BuildContext
) : TagConsumer<R>, Builder {

    val path = arrayListOf<VNode>()
    private var lastLeaved : VNode? = null

    override val builderContextTree: ArrayDeque<BuildContext> = ArrayDeque(1)
    override var currentBuildContext: BuildContext = rootBuildContext

    init {
        builderContextTree.addFirst(currentBuildContext)
    }

    override fun onTagStart(tag: Tag) {
        val vNode = SnabbdomH.h(tag.tagName)

        tag.attributesEntries.forEach {
            setProperties(vNode, it.key, it.value)
        }

        // Add VNode as a child
        if (path.isNotEmpty()) {
            path.last().addChild(vNode)
        }

        path.add(vNode)
    }

    private fun setProperties(vNode: VNode, key: String, value: String?) {
        if (key == CLASS_ATTR) {
            if (value == null)
                vNode.removeAttr(CLASS_ATTR)
//                vNode.removeClasses()
            else {
                vNode.addAttr(CLASS_ATTR, value)
//                value.split(" ").forEach {
//                    vNode.addClass(it)
//                }
            }
        }
        else {
            if (value == null)
                vNode.removeAttr(key)
            else
                vNode.addAttr(key, value)
        }
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        when {
            path.isEmpty() -> throw IllegalStateException("No current tag")
            path.last().sel!!.toLowerCase() != tag.tagName.toLowerCase() -> throw IllegalStateException("Wrong current tag")
            else -> setProperties(path.last(), attribute, value)
        }
    }

    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
        when {
            path.isEmpty() -> throw IllegalStateException("No current tag")
            path.last().sel!!.toLowerCase() != tag.tagName.toLowerCase() -> throw IllegalStateException("Wrong current tag")
            else -> path.last().setEvent(event, value)
        }
    }

    override fun onTagEnd(tag: Tag) {
        if (path.isEmpty() || path.last().sel!!.toLowerCase() != tag.tagName.toLowerCase()) {
            throw IllegalStateException("We haven't entered tag ${tag.tagName} but trying to leave")
        }

        val vNode = path.last()

        ascend(vNode, tag)

        lastLeaved = path.removeAt(path.lastIndex)
    }

    override fun onTagContent(content: CharSequence) {
        if (path.isEmpty()) {
            throw IllegalStateException("No current DOM node")
        }

        if (path.last().text != null) {
            println("CONTENT AND CHILD ")
        }

        path.last().text = content.toString()
//        path.last().appendChild(document.createTextNode(content.toString()))
    }

    override fun onTagContentEntity(entity: Entities) {
//        if (path.isEmpty()) {
//            throw IllegalStateException("No current DOM node")
//        }
//
//        // stupid hack as browsers doesn't support createEntityReference
//        val s = document.createElement("span") as HTMLElement
//        s.innerHTML = entity.text
//        path.last().appendChild(s.childNodes.asList().filter { it.nodeType == Node.TEXT_NODE }.first())

        // other solution would be
//        pathLast().innerHTML += entity.text
    }

    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
//        with(DefaultUnsafe()) {
//            block()
//
//            path.last().innerHTML += toString()
//        }
    }


    override fun onTagComment(content: CharSequence) {
//        if (path.isEmpty()) {
//            throw IllegalStateException("No current DOM node")
//        }
//
//        path.last().appendChild(document.createComment(content.toString()))
    }

    override fun finalize(): R = lastLeaved as? R ?: throw IllegalStateException("We can't finalize as there was no tags")

//    @Suppress("UNCHECKED_CAST")
//    private fun HTMLElement.asR(): R = this.asDynamic()

}


fun buildVDom(context: BuildContext) : TagConsumer<VNode> = SnabbdomBuilder(context)



//fun Document.createTree(context: BuildContext) : TagConsumer<HTMLElement> = JSDOMBuilder(this, context)
//fun Document.create(context: BuildContext) : TagConsumer<HTMLElement> = JSDOMBuilder(this, context)
//
//fun Node.append(context: BuildContext, block: TagConsumer<HTMLElement>.() -> Unit): List<HTMLElement> =
//    ArrayList<HTMLElement>().let { result ->
//        ownerDocumentExt.createTree(context).onFinalize { it, partial ->
//            if (!partial) {
//                result.add(it); appendChild(it)
//            }
//        }.block()
//
//        result
//    }
//
//fun Node.prepend(context: BuildContext, block: TagConsumer<HTMLElement>.() -> Unit): List<HTMLElement> =
//    ArrayList<HTMLElement>().let { result ->
//        ownerDocumentExt.createTree(context).onFinalize { it, partial ->
//            if (!partial) {
//                result.add(it)
//                insertBefore(it, firstChild)
//            }
//        }.block()
//
//        result
//    }
//
////val HTMLElement.append: TagConsumer<HTMLElement>
////    get() = ownerDocumentExt.createTree().onFinalize { element, partial ->
////        if (!partial) {
////            this@append.appendChild(element)
////        }
////    }
//
////val HTMLElement.prepend: TagConsumer<HTMLElement>
////    get() = ownerDocumentExt.createTree().onFinalize { element, partial ->
////        if (!partial) {
////            this@prepend.insertBefore(element, this@prepend.firstChild)
////        }
////    }
//
//private val Node.ownerDocumentExt: Document
//    get() = when {
//        this is Document -> this
//        else -> ownerDocument ?: throw IllegalStateException("Node has no ownerDocument")
//    }
