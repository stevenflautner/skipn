package io.skipn

object Skipn {
    val buildHash = this::class.java.getResource("/skipn_meta.json").readText()
}