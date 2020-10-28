package io.skipn.platform

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.getContextualOrDefault
import io.skipn.utils.require

actual fun loadResourceString(src: String): String {
    return require(src)
        .default.toString()
}

actual class SkipnResources actual constructor() {
    var id: Int = 0
    lateinit var resources: JsonObject

    inline fun <reified T : Any> get() : T {
        val jsonElement = resources[id.toString()] ?: throw Exception("" +
                "No skipn resource was found at id: $id\n" +
                "when trying to load ${T::class}")
        id++

        // TODO Update once overload resolution ambiguity is resolved
        return Json.decodeFromJsonElement(
                Json.serializersModule.getContextualOrDefault(),
                jsonElement)
    }

    fun init(json: String) {
        // TODO Update once overload resolution ambiguity is resolved
        resources = Json.decodeFromString(JsonObject.serializer(), json)
    }
}