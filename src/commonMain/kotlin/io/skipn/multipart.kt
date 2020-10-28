package io.skipn

import io.ktor.http.*

class FileData(val bytes: ByteArray, val type: ContentType?)
typealias FileList = ArrayList<FileData>