package io.skipn.utils

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

// TODO REVISE THIS SOMETIMES
// This should be a function otherwise if it's declared commonly
// as a variable JS throws undefined.
fun buildApiJson() = Json {
    classDiscriminator = "type"
}

// This should be called because of a bug
// https://youtrack.jetbrains.com/issue/KT-41282
// where module serializer throws an error in js
// Replace it with the regular Json.encodeToString
// once fixed and remove this function
@OptIn(InternalSerializationApi::class)
inline fun <reified T: Any> Json.encodeToStringStatic(data: T): String {
    return encodeToString(T::class.serializer(), data)
}

// This should be called because of a bug
// https://youtrack.jetbrains.com/issue/KT-41282
// where module serializer throws an error in js
// Replace it with the regular Json.decodeFromString
// once fixed and remove this function
@OptIn(InternalSerializationApi::class)
inline fun <reified T: Any> Json.decodeFromStringStatic(data: String): T {
    return decodeFromString(T::class.serializer(), data)
}