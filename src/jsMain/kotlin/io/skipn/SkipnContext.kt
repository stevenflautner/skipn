package io.skipn

import VNode
import io.skipn.builder.BuildContext
import io.skipn.platform.DEV
import io.skipn.platform.SkipnResources
import io.skipn.utils.byId
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.html.FlowContent
import kotlinx.html.Tag
import org.w3c.dom.Element
import snabbdom.SnabbdomBuilder
import kotlin.js.Date
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

fun Tag.getUnderlyingHtmlElement(): VNode {
    val consumer = this.consumer as SnabbdomBuilder<VNode>
    return consumer.path.last()
}

fun Tag.prepareElement(): VNode {
    val context = skipnContext

    val id = attributes["id"] ?: context.points.generateId().also {
        attributes["id"] = it
    }

    return getUnderlyingHtmlElement()

//    return if (context.isInitializing && !DEV)
//        byId(id)
//    else getUnderlyingHtmlElement()
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


typealias DeviceFunction = suspend CoroutineScope.() -> Unit

class DeviceActionBucket (
    val buildContext: BuildContext,
    action: DeviceFunction
) {
    val coroutineScope = CoroutineScope(SupervisorJob(buildContext.getCoroutineScope().coroutineContext.job))
    val actions = listOf(action)

    fun launchAll() {
        actions.forEach {
            coroutineScope.launch {
                it()
            }
        }
    }
}

class Device {
    private val desktop = arrayListOf<DeviceActionBucket>()
    private val mobile = arrayListOf<DeviceActionBucket>()

    private lateinit var current: List<DeviceActionBucket>

    fun runOnDesktop(context: BuildContext, action: DeviceFunction) {
        add(context, action, desktop)
    }
    fun runOnMobile(context: BuildContext, action: DeviceFunction) {
        add(context, action, mobile)
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
            it.coroutineScope.coroutineContext.cancelChildren()
        }

        current = new

        // Launch new coroutines for desktop or mobile
        new.forEach { bucket ->
            bucket.launchAll()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun add(context: BuildContext, action: DeviceFunction, target: ArrayList<DeviceActionBucket>) {
        val targetScope = context.getCoroutineScope()

        ensureRunAfterInitialization {
            if (context.getCoroutineScope() != targetScope) return@ensureRunAfterInitialization

            val bucket = DeviceActionBucket(context, action)
            target.add(bucket)

            // Execute if current equals target
            if (current === target) {
                bucket.launchAll()
            }

            // Remove if the coroutine got cancelled
            context.launch {
                try {
                    delay(Duration.INFINITE)
                } catch (e: CancellationException) {
                    target.remove(bucket)
                }
            }
        }
    }
}

inline fun createSkipnContext(body: (SkipnContext) -> Unit): SkipnContext {
    val ct = Date().getMilliseconds()

    val route = "${window.location.pathname}${window.location.search}"

    return SkipnContext(route).apply {
        body(this)
        isInitializing = false
        if (DEV) {
            println("Skipn initialized, took ${Date().getMilliseconds() - ct} ms")
        }
    }
}