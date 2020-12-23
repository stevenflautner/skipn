package io.skipn.builder

import kotlinx.html.FlowContent

expect interface Builder {

    val rootBuildContext: BuildContext
    var currentBuildContext: BuildContext

}

expect val FlowContent.builder: Builder