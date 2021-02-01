import io.skipn.Endpoint
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

//@OptIn(InternalSerializationApi::class)
//inline fun <reified REQ : Any, reified RESP : Any> endpointFunc(
//    endpoint: Endpoint<REQ, RESP>,
//    request: REQ
//): suspend () -> RESP = {
//    val post = api.post<String>(endpoint.route) {
//        contentType(ContentType.Application.Json)
//
//        body = Json.encodeToString(request)
//    }
//
//    Json.decodeFromString(RESP::class.serializer(), post)
////    Json.decodeFromString(Json.serializersModule.getContextualOrDefault(), post)
//}
