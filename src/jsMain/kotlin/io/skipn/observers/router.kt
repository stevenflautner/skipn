//package io.skipn.observers
//
//import io.skipn.Snabbdom
//import io.skipn.builder.buildContext
//import io.skipn.builder.builder
//import io.skipn.prepareElement
//import io.skipn.skipnContext
//import kotlinx.coroutines.flow.SharedFlow
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.flow.drop
//import kotlinx.coroutines.launch
//import kotlinx.html.*
//import org.w3c.dom.HTMLCanvasElement
//import snabbdom.buildVDom
//
//@HtmlTagMarker
//actual fun FlowContent.router(node: DIV.(String?) -> Unit) {
//    div {
//        var element = prepareElement()
//        val parentContext = buildContext
//        val parentScope = parentContext.getCoroutineScope()
//
//        val route = skipnContext.router.routeFor(parentContext.getRouteLevel())
//
//        // Creates a new Build Context
//        // as a copy of the current one
//        val context = builder.createContextAndDescend(element, parentContext.getRouteLevel() + 1)
//        this.buildContextBuilt = true
//
//        // Run node first
//        node(route)
//
//        parentScope.launch {
//            val drop = (flow as? SharedFlow)?.replayCache?.size ?: 0
//            // Drop all values in the replay cache
//            // So we only notify newly emitted values
//            flow.drop(drop).collect {
//                context.cancelAndCreateScope(parentScope)
//
//                val newVNode = buildVDom(context).let { newConsumer ->
//                    tag.depId = 0
//                    tag.consumer = newConsumer
//                    tag.visitAndFinalize(newConsumer, block)
//                }
//                vNode = Snabbdom.patch(vNode, newVNode)
//            }
//        }
//
//
//
////        // Listen for changes of current route level
////        parentScope.launch {
////            skipnContext.router.filterRouteChangesFor(parentContext.getRouteLevel()).collect { change ->
////                context.cancelAndCreateScope(parentScope)
////                context.getCoroutineScope().launch {
////                    element = replaceElement(element, context) {
////                        node(change)
////                    }
////                }
////            }
////        }
//    }
//}