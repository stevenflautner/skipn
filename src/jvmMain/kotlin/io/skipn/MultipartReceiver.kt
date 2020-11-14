package io.skipn

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.request.*
import kotlinx.datetime.LocalDateTime
import java.io.ByteArrayOutputStream
import java.lang.Exception
import kotlin.reflect.full.*

fun parseEmpty(value: String?): String? {
    if (value == "") return null
    return value
}

fun refuseEmpty(value: String): String {
    if (value == "") throw Exception("Should not be null or empty for $value")
    return value
}

//fun Map<String, Any>.parse(key: String): String? {
//    val string = get(key) as? String
//    if (string != null && string.isEmpty())
//        return null
//    return string
//}

//val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

suspend inline fun <reified T: Any> ApplicationCall.parseMultipart(): Map<String, Any?> {
    val data = hashMapOf<String, Any?>()
    val multipart = receiveMultipart()

    while (true) {
        val part = multipart.readPart() ?: break
        when (part) {
            is PartData.FormItem -> {
                val name = part.name!!
                val elem = data[name]

                val param = T::class.primaryConstructor!!.findParameterByName(name) ?: throw Exception("No parameter for type: $name")

                when (param.type) {
                    Int::class.starProjectedType -> {
                        data[name] = refuseEmpty(part.value).toInt()
                    }
                    Int::class.starProjectedType.withNullability(true) -> {
                        data[name] = parseEmpty(part.value)?.toInt()
                    }
                    String::class.starProjectedType -> {
                        data[name] = refuseEmpty(part.value)
                    }
                    Double::class.starProjectedType -> {
                        data[name] = refuseEmpty(part.value).toDouble()
                    }
                    Double::class.starProjectedType.withNullability(true) -> {
                        data[name] = parseEmpty(part.value)?.toDouble()
                    }
                    LocalDateTime::class.starProjectedType -> {
                        data[name] = LocalDateTime.parse(refuseEmpty(part.value))
                    }
                    LocalDateTime::class.starProjectedType.withNullability(true) -> {
                        data[name] = parseEmpty(part.value)?.let {
                            LocalDateTime.parse(refuseEmpty(part.value))
                        }
                    }
                    String::class.starProjectedType.withNullability(true) -> {
                        data[name] = parseEmpty(part.value)
                    }
                    else -> {
                        if (param.type.isSubtypeOf(List::class.starProjectedType)
                            || param.type.isSubtypeOf(List::class.starProjectedType.withNullability(true))) {

                            if (elem == null)
                                data[name] = arrayListOf(part.value)
                            else
                                (elem as ArrayList<Any>).add(part.value)
                        } else {
                            data[name] = part.value
                        }
                    }
                }
            }
            is PartData.FileItem -> {
                val outputStream = ByteArrayOutputStream()
                part.streamProvider().use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                val bytes = outputStream.toByteArray()
                if (bytes.isNotEmpty()) {
                    // Handle multiple files uploaded with same input name
                    val files = data[part.name!!] as? FileList
                        ?: run {
                            val files = FileList()
                            data[part.name!!] = files
                            files
                        }
                    files.add(FileData(
                        bytes,
                        part.contentType
                    ))
                }
            }
        }
        part.dispose()
    }

    return data
}

inline fun <reified T: Any> convertMapToInstance(data: Map<String, Any?>): T {
    val params = arrayListOf<Any?>()
    T::class.primaryConstructor!!.parameters.forEach { constructorParam ->
        val elem = data[constructorParam.name]
        if (elem != null) {
            params.add(elem)
        } else {
            if (constructorParam.type.isMarkedNullable)
                params.add(null)
            else
                throw Exception("Received null for not nullable type")
        }
    }
    return T::class.primaryConstructor!!.call(*params.toArray())
}