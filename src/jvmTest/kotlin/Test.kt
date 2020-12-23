import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
//    CoroutineScope
//
//    runBlocking {
//        launch {
//
//        }
//    }
//    val state = MutableStateFlow(0)
//
//    launch(Dispatchers.Default) {
//        state.subscriptionCount.collect {
//            println("SUBS: $it")
//        }
//    }
//
//    val job = launch(Dispatchers.Default) {
//        state.collect {
//            println(it)
//        }
//    }
//    state.value = 12
//    delay(100)
//    job.cancel()
//    state.value = 55
//    delay(100)


//    while (true) {}

    val b = URLBuilder("")


//    val a = "shopId=1222".parseUrlEncodedParameters()
//    val r = parseQueryString("localhost:8080/shop?shopId=1222")
//    println(r.get("shopId"))
//    println(r.getAll("shopId"))
//    r.forEach { s, list ->
//        println("HA")
//        println(s)
//        println(list)
//    }
//    println(a)
    println(b.parameters["shopId"])

}