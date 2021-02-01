import io.skipn.builder.ParsedRoute
import io.skipn.builder.formUrlEncode
import kotlin.test.Test
import kotlin.test.assertTrue

class Utilities {
    @Test
    fun routeParsing() {
        val route = "/shop?item=testItem22&otherItem=8666^%$#@"

        val parsed = ParsedRoute(route)
        assertTrue { parsed.routeValues.size == 1 }
        assertTrue { parsed.routeValues[0] == "shop" }

        assertTrue { parsed.parameters.size == 2 }
        assertTrue { parsed.parameters["item"] == "testItem22" }
        assertTrue { parsed.parameters["otherItem"] == "8666^%\$#@" }

        println(parsed.parameters.formUrlEncode())
        assertTrue { parsed.parameters.formUrlEncode() == "&item=testItem22&otherItem=8666%5E%25%24%23%40" }
    }
}