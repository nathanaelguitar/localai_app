package com.nathanael.canopy.platform

expect class PlatformPaths {
    fun modelsDirectory(): String
    fun fileExists(path: String): Boolean
}
