package kotlinx.html

import kotlinx.coroutines.flow.Flow
import kotlinx.html.impl.*

open class HTMLTag(
    override val tagName: String,
    override var consumer: TagConsumer<*>,
    initialAttributes: Map<String, String>,
    override val namespace: String? = null,
    override val inlineTag: Boolean,
    override val emptyTag: Boolean
) : Tag {

    override val attributes: DelegatingMap = DelegatingMap(initialAttributes, this) { consumer }

    override val attributesEntries: Collection<Map.Entry<String, String>>
        get() = attributes.immutableEntries


//    val dependsOn: ArrayList<Flow<Any>> by lazy {
//        val v = arrayListOf<Flow<Any>>()
//        hasDependencies = true
//        v
//    }

    var depId = 0
    private var _dependencies: ArrayList<Any>? = null
    var dependenciesBuilt: Boolean = false

    fun addDependency(flow: Flow<*>) {
        val dependencies = _dependencies ?: arrayListOf<Any>().also { _dependencies = it }
        dependencies.add(flow as Any)
    }

    val dependencies get() = _dependencies as? ArrayList<Flow<*>>
}
