package com.sohohouse.seven.common.prefs

import java.io.File
import javax.inject.Inject

interface FileCreator {
    fun file(parent: File, fileName: String): File

    class Impl @Inject constructor() : FileCreator {
        override fun file(parent: File, fileName: String): File {
            return File(parent, fileName)
        }
    }
}