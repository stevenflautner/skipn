import org.w3c.dom.Node
import org.w3c.dom.events.Event

//@file:JsModule("snabbdom")
//@file:JsNonModule

//@JsModule("snabbdom/build/package/vnode")
//@JsNonModule()
//external class VNode()

//@JsModule("snabbdom/build/package/vnode.js")
//@JsNonModule()
//external class VNodeData()

//@JsModule("snabbdom/build/package/h.js")
//@JsNonModule()
//external fun h(): Unit

//export function h (sel: string, data: VNodeData | null, children: VNodeChildren): VNode
//external fun h(tag: String, init: Map<String, dynamic>, content: String): dynamic
//external fun h(sel: String, init: Map<String, dynamic>, content: String): VNode

//@JsModule("snabbdom/build/package/init")
//@JsNonModule
//external fun init(a: dynamic = definedExternally, b: dynamic = definedExternally): dynamic

@JsModule("snabbdom/build/package/vnode")
@JsNonModule()
external interface VNode {
    var sel: String?
    var data: VNodeData?
    var children: Array<dynamic /* VNode | String */>?
    var elm: Node?
    var text: String?
    var key: dynamic /* String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
}

typealias Classes = Map<String, Boolean>
typealias Attrs = Map<String, dynamic /* String | Number | Boolean */>
typealias Props = Map<String, Any>

class VNodeDataClass : VNodeData {
//    fun classes {
//        `class` =
//    }
    inline fun jsObject(body: (dynamic) -> Unit) = object {}.also {
        body(it.asDynamic())
    }
}



@JsModule("snabbdom/build/package/vnode")
@JsNonModule()
external interface VNodeData {
    var props: dynamic
        get() = definedExternally
        set(value) = definedExternally
    var attrs: dynamic
        get() = definedExternally
        set(value) = definedExternally
    var `class`: dynamic
        get() = definedExternally
        set(value) = definedExternally
//    var style: Record<String, String>? /* Record<String, String>? & `T$4`? */
//        get() = definedExternally
//        set(value) = definedExternally
//    var dataset: Dataset?
//        get() = definedExternally
//        set(value) = definedExternally
    var on: Any? /* Any? & `T$5`? */
        get() = definedExternally
        set(value) = definedExternally
//    var hero: Hero?
//        get() = definedExternally
//        set(value) = definedExternally
//    var attachData: AttachData?
//        get() = definedExternally
//        set(value) = definedExternally
    var hook: dynamic?
        get() = definedExternally
        set(value) = definedExternally
    var key: dynamic /* String? | Number? */
        get() = definedExternally
        set(value) = definedExternally
    var ns: String?
        get() = definedExternally
        set(value) = definedExternally
    var fn: (() -> VNode)?
        get() = definedExternally
        set(value) = definedExternally
    var args: Array<Any>?
        get() = definedExternally
        set(value) = definedExternally

//    operator fun get(key: String): Any?
//    operator fun set(key: String, value: Any)
}


//@JsModule("snabbdom/build/package/modules/class")
//@JsNonModule
//external class SnabbdomClass

//@JsModule("snabbdom/build/package/modules/class")
//@JsNonModule
//external val classModule: dynamic

/**
 * Modules of Snabbdom
 */
@JsModule("snabbdom/build/package/modules/class")
@JsNonModule
external object SnabbdomClassesModule {
    val classModule: Any
}
@JsModule("snabbdom/build/package/modules/attributes")
@JsNonModule
external object SnabbdomAttributesModule {
    val attributesModule: Any
}
@JsModule("snabbdom/build/package/modules/style")
@JsNonModule
external object SnabbdomStyleModule {
    val styleModule: Any
}
@JsModule("snabbdom/build/package/modules/eventlisteners")
@JsNonModule
external object SnabbdomEventListenersModule {
    val eventListenersModule: Any
}


@JsModule("snabbdom/build/package/init")
@JsNonModule
external object SnabbdomInit {
    fun init(modules: Array<Any>): (oldVnode: dynamic /* VNode | Element */, vnode: VNode) -> VNode


//    fun h(sel: String): VNode

    fun h(sel: String, data: VNodeData, children: Array<VNode>?): VNode
//external fun h(sel: String, data: VNodeData?, children: dynamic / typealias VNodeChildren = dynamic */): VNode
}

fun VNode.getData(): VNodeData = data ?: VNodeDataClass().also {
    data = it
}

fun VNode.addAttr(name: String, value: String?) {
    val data = getData()
    val attrs: dynamic = data.attrs ?: object {}.also { data.attrs = it }
    attrs[name] = value
}
fun VNode.removeAttr(name: String) {
    val data = this.data ?: return
    val attrs = data.attrs ?: return
//    attrs[name] = value
    println("REMOVE ATTRIBUTE SHOULD BE HANDLED")
//    js("delete attrs[name]")
}
//fun VNode.changeAttr(name: String, value: String?) {
//    data!!.attrs!![name] = value
//}
fun VNode.removeClasses() {
    data!!.`class` = null
}
fun VNode.addClass(name: String) {
    val data = getData()
    val classes: dynamic = data.`class` ?: object {}.also { data.`class` = it }
    classes[name] = true
}

fun VNode.setEvent(event: String, value: (Event) -> Unit) {
    val data = getData()
    val on: dynamic = data.on ?: object {}.also { data.on = it }
    on[event] = value
}

fun VNode.addChild(vNode: VNode) {
//    val children: ArrayList<dynamic> = this.children ?: arrayListOf<dynamic>().also {
//        this.children = it
//    }
    val children = children
    if (children == null)
        this.children = arrayOf(vNode)
    else
        this.children = children + vNode
//    this.children = arrayOf(vNode)
//    children.add(vNode)
}
fun VNode.addHook(key: String, hook: (dynamic, dynamic) -> Unit) {
    val data = getData()
    val hooks: dynamic = data.hook ?: object {}.also { data.hook = it }
    hooks[key] = hook
}

@JsModule("snabbdom/build/package/h")
@JsNonModule
external object SnabbdomH {

    fun h(
        sel: String,
        data: dynamic = definedExternally,
        children: dynamic = definedExternally,
//        text: String? = definedExternally,
    ): VNode

}

@JsModule("snabbdom/build/package/tovnode")
@JsNonModule
external object SnabbdomToVNode {

    fun toVNode(
        node: Node
    ): VNode

}
//external fun init(modules: Array<Any>, domApi: dynamic = definedExternally): (oldVnode: dynamic /* VNode | Element */, vnode: VNode) -> VNode