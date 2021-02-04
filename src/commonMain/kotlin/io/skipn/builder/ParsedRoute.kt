package io.skipn.builder

typealias Parameters = HashMap<String, String>

expect fun String.encodeURLParameter(): String

fun Parameters.formUrlEncode(): String {
    return StringBuilder().apply {
        entries.forEachIndexed { index, entry ->
            val key = entry.key.encodeURLParameter()
            val value = entry.value.encodeURLParameter()

            if (index > 0) append('&')
            append("$key=$value")
        }
    }.toString()
}

class ParsedRoute(raw: String) {

    val routeValues: List<String>
    val parameters: Parameters

    init {
        val split = raw.split("?")
        routeValues = parseRoute(split)
        parameters = parseParameters(split)
    }

    private fun parseParameters(split: List<String>): Parameters = Parameters().apply {
        split.getOrNull(1)?.let { query ->
            val pairs: List<String> = query.split("&")

            for (pair in pairs) {
                val idx = pair.indexOf("=")
                put(pair.substring(0, idx), pair.substring(idx + 1))
            }
        }
    }

    private fun parseRoute(split: List<String>): List<String> {
        var routePart = split[0]

        val fullRoute = routePart.removePrefix("/")
        val count = fullRoute.count { it == '/' }

        return if (count == 0) {
            listOf(fullRoute)
        } else {
            fullRoute.split("/")
        }
    }
}