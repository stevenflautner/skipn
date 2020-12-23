package io.skipn.actions

expect fun routePage(route: String)

expect fun changeParameter(key: String, parameter: String)

internal expect fun updateUrlParameter(parameters: String)