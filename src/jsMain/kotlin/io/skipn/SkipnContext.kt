package io.skipn

import io.skipn.builder.BuildContext
import io.skipn.builder.DeviceFunction
import io.skipn.html.JSDOMBuilder
import io.skipn.observers.Scope
import io.skipn.platform.DEV
import io.skipn.platform.SkipnResources
import io.skipn.utils.byId
import kotlinx.browser.window
import kotlinx.html.FlowContent
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import kotlin.js.Date
import kotlin.time.ExperimentalTime

fun FlowContent.getUnderlyingHtmlElement(): HTMLElement {
    val consumer = this.consumer as JSDOMBuilder
    return consumer.path[consumer.path.lastIndex]

//    var d = this.consumer.asDynamic()
//    if (d.downstream != null) {
//        d = d.downstream
//    }
//    val arr = d.path as Array<HTMLElement>
////    val arr = d.path_0.toArray() as Array<HTMLElement>
//    return arr[arr.size - 1]
}

fun FlowContent.prepareElement(): Element {
    val context = skipnContext

    val id = attributes["id"] ?: context.points.generateId().also {
        attributes["id"] = it
    }

    return if (context.isInitializing && !DEV)
        byId(id)
    else getUnderlyingHtmlElement()
}

actual class SkipnContext(route: String) : SkipnContextBase(route) {

    actual var isInitializing = true
    actual val points = Elements()
    actual val resources = SkipnResources()
    val device = Device()

    inline fun initialize(body: () -> Unit) {
        val ct = Date().getMilliseconds()
        body()
        isInitializing = false
        if (DEV) {
            println("Skipn initialized, took ${Date().getMilliseconds() - ct} ms")
        }
    }

}


class DeviceActionBucket (
    val scope: Scope,
    action: DeviceFunction
) {
    val actions = listOf(action)

    fun runAll() {
        actions.forEach {
            scope.it()
        }
    }
}

class Device {
    private val desktop = arrayListOf<DeviceActionBucket>()
    private val mobile = arrayListOf<DeviceActionBucket>()

    private lateinit var current: List<DeviceActionBucket>

    fun runOnDesktop(scope: Scope, action: DeviceFunction) {
        add(scope, action, desktop)
    }
    fun runOnMobile(scope: Scope, action: DeviceFunction) {
        add(scope, action, mobile)
    }

    init {
        Skipn.runAfterInitialized!!.add {
            current = if (window.innerWidth < 768) mobile else desktop

            window.addEventListener("resize", {
                val mobileWidth = window.innerWidth < 768

                if (current === mobile) {
                    if (!mobileWidth)
                        changeCurrent(desktop)
                }
                else {
                    if (mobileWidth)
                        changeCurrent(mobile)
                }
            })
        }
    }

    private fun changeCurrent(new: List<DeviceActionBucket>) {
        // Cancel children coroutines
        current.forEach {
            it.scope.disposeChildren()
        }

        current = new

        // Launch new coroutines for desktop or mobile
        new.forEach { bucket ->
            bucket.runAll()
        }
    }

    private fun add(scope: Scope, action: DeviceFunction, target: ArrayList<DeviceActionBucket>) {
        ensureRunAfterInitialization {
            val bucket = DeviceActionBucket(scope, action)
            target.add(bucket)

            // Execute if current equals target
            if (current === target) {
                bucket.runAll()
            }

            scope.onDispose {
                target.remove(bucket)
            }
        }
    }
}

inline fun createSkipnContext(body: (SkipnContext) -> Unit): SkipnContext {
    val ct = Date().getMilliseconds()

    return SkipnContext(window.location.href).apply {
        body(this)
        isInitializing = false
        if (DEV) {
            println("Skipn initialized, took ${Date().getMilliseconds() - ct} ms")
        }
    }
}