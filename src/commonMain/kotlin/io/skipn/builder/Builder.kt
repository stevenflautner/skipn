package io.skipn.builder

import kotlinx.html.FlowContent
import kotlinx.html.Tag

expect interface Builder {

    val rootBuildContext: BuildContext
    var currentBuildContext: BuildContext

}

expect val Tag.builder: Builder