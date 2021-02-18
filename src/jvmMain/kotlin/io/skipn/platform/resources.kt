package io.skipn.platform

import io.skipn.utils.encodeToStringStatic
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement

actual class SkipnResources actual constructor() {

    var id: Int = 0
    val resources = mutableMapOf<String, JsonElement>()

    inline fun <reified T : Any> add(resource: T) {
        resources[id.toString()] = Json.encodeToJsonElement(resource)
        id++
    }

    fun createSnapshot(): String {
        return Json.encodeToString(JsonObject(resources))
    }
}