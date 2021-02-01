package io.skipn

// TODO FILE CONTENT TYPE COMMON
class FileData(val bytes: ByteArray, val type: Int?)
//class FileData(val bytes: ByteArray, val type: ContentType?)
typealias FileList = ArrayList<FileData>