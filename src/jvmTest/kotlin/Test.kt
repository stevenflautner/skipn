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

    Navbar(this)

}

fun Navbar(ctx: CoroutineScope) {
        println("111")
    ctx.launch {
        asd()
    }
        println("33")
}
suspend fun asd() {
    delay(100)
    println("BLOCKK")
}