package io.skipn.builder

import kotlinx.html.FlowContent

expect interface Builder {

    val rootBuildContext: BuildContext
    var currentBuildContext: BuildContext?

    fun getBuildContext(): BuildContext

}

expect val FlowContent.builder: Builder