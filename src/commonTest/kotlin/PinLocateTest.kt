import io.skipn.events.onClick
import io.skipn.provide.locate
import io.skipn.provide.locateOrNull
import io.skipn.provide.pin
import io.skipn.skipnContext
import kotlinx.html.button
import kotlinx.html.div

fun main() {
    //        div {
//            pin(MutableStateFlow(10))
//
//            + "HELLO"
//            button {
//                val ds: MutableStateFlow<Int>? = locateStateFlowOrNull()
//                println("kh")
//                println(ds?.value)
//                onClick {
//
//                }
//                +"Button"
//                div {
//                    val ds1 = pin(DashboardTabsState(skipnContext))
//                    ds1.tab.value = DashboardTabs.PROFILE_DATA
//                    div {
//                        val d33s = locate<DashboardTabsState>()
//                        println(d33s.tab.value)
//                    }
//                }
//                div {
//                    pin(MutableStateFlow(100))
//                    div {
//                        val ds1: MutableStateFlow<Int> = locateStateFlow()
//                        println("khOOO")
//                        println(ds1.value)
//                    }
////                    val d33s = locate<DashboardTabsState>()
////                    println("SHOULD BE ORDER HISTORY")
////                    println(d33s.tab.value)
//                }
//            }
//        }
//        div {
//            val ds = locateOrNull<DashboardTabsState>()
//            println(ds?.tab?.value)
//        }
//    div {
//        pin(DashboardTabsState(skipnContext))
//
//        + "HELLO"
//        button {
//            val ds = locate<DashboardTabsState>()
//            println(ds.tab.value)
//            onClick {
//
//            }
//            +"Button"
//            div {
//                val ds1 = pin(DashboardTabsState(skipnContext))
//                ds1.tab.value = DashboardTabs.PROFILE_DATA
//                div {
//                    val d33s = locate<DashboardTabsState>()
//                    println(d33s.tab.value)
//                }
//            }
//            div {
//                val d33s = locate<DashboardTabsState>()
//                println("SHOULD BE ORDER HISTORY")
//                println(d33s.tab.value)
//            }
//        }
//    }
//    div {
//        val ds = locateOrNull<DashboardTabsState>()
//        println(ds?.tab?.value)
//    }
}