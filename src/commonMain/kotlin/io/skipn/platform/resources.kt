package io.skipn.platform

// A resource can be loaded as a string if the appropriate
// file extension has been added to raw-loader in webpack
expect fun loadResourceString(src: String): String
expect class SkipnResources()