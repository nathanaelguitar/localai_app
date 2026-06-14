package com.nathanael.canopy.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual class PlatformPaths {
    actual fun modelsDirectory(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory, NSUserDomainMask, true
        )
        val documentsDir = paths.first() as String
        val modelsDir = "$documentsDir/models"
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(modelsDir)) {
            fileManager.createDirectoryAtPath(modelsDir, true, null, null)
        }
        return modelsDir
    }

    actual fun fileExists(path: String): Boolean {
        return NSFileManager.defaultManager.fileExistsAtPath(path)
    }
}
