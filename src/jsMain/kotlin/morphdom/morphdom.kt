//@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
//
//package morphdom
//
//import kotlin.js.*
//import org.w3c.dom.*
//
//@JsModule("morphdom/dist/morphdom-umd.js")
//@JsNonModule
//external interface MorphDomOptions {
//    var getNodeKey: ((node: Node) -> Any)?
//        get() = definedExternally
//        set(value) = definedExternally
//    var onBeforeNodeAdded: ((node: Node) -> Node)?
//        get() = definedExternally
//        set(value) = definedExternally
//    var onNodeAdded: ((node: Node) -> Node)?
//        get() = definedExternally
//        set(value) = definedExternally
//    var onBeforeElUpdated: ((fromEl: HTMLElement, toEl: HTMLElement) -> Boolean)?
//        get() = definedExternally
//        set(value) = definedExternally
//    var onElUpdated: ((el: HTMLElement) -> Unit)?
//        get() = definedExternally
//        set(value) = definedExternally
//    var onBeforeNodeDiscarded: ((node: Node) -> Boolean)?
//        get() = definedExternally
//        set(value) = definedExternally
//    var onNodeDiscarded: ((node: Node) -> Unit)?
//        get() = definedExternally
//        set(value) = definedExternally
//    var onBeforeElChildrenUpdated: ((fromEl: HTMLElement, toEl: HTMLElement) -> Boolean)?
//        get() = definedExternally
//        set(value) = definedExternally
//    var childrenOnly: Boolean?
//        get() = definedExternally
//        set(value) = definedExternally
//}
//
//@JsModule("morphdom/dist/morphdom-umd.js")
//@JsNonModule
//external fun morphdom(fromNode: Node, toNode: Node, options: MorphDomOptions = definedExternally): Node
//
//@JsModule("morphdom/dist/morphdom-umd.js")
//@JsNonModule
//external fun morphdom(fromNode: Node, toNode: Node): Node
//
////@JsModule("morphdom/dist/morphdom-umd.js")
////@JsNonModule
////external fun morphdom(fromNode: Node, toNode: String, options: MorphDomOptions = definedExternally)
////
////@JsModule("morphdom")
////external fun morphdom(fromNode: Node, toNode: String)