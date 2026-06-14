package com.nathanael.canopy.platform

import android.content.Context
import java.io.File

actual class PlatformPaths(private val context: Context) {
    actual fun modelsDirectory(): String {
        val dir = File(context.filesDir, "models")
        if (!dir.exists()) dir.mkdirs()
        return dir.absolutePath
    }

    actual fun fileExists(path: String): Boolean {
        return File(path).exists()
    }
}
