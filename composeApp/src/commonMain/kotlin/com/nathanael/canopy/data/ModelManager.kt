package com.nathanael.canopy.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nathanael.canopy.platform.PlatformPaths
import com.nathanael.canopy.platform.platformDownloadFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

enum class ModelStatus {
    NotDownloaded,
    Downloading,
    Downloaded,
    LoadingIntoMemory,
    Ready,
    Error
}

class ModelManager(
    private val paths: PlatformPaths,
    private val dbRepo: DatabaseRepository
) {
    var status by mutableStateOf(ModelStatus.NotDownloaded)
        private set
    var downloadProgress by mutableStateOf(0f)
        private set
    var errorMessage by mutableStateOf("")
        private set

    val modelFileName = "google_gemma-4-E2B-it-Q4_K_M.gguf"

    private val modelDownloadUrl =
        "https://huggingface.co/bartowski/google_gemma-4-E2B-it-GGUF/resolve/main/$modelFileName"

    fun modelPath(): String = "${paths.modelsDirectory()}/$modelFileName"

    fun checkModelStatus() {
        val path = modelPath()
        status = when {
            dbRepo.isModelDownloaded() && paths.fileExists(path) -> ModelStatus.Downloaded
            else -> ModelStatus.NotDownloaded
        }
    }

    suspend fun downloadModel() {
        status = ModelStatus.Downloading
        downloadProgress = 0f
        errorMessage = ""

        try {
            val destPath = modelPath()
            withContext(Dispatchers.IO) {
                platformDownloadFile(
                    url = modelDownloadUrl,
                    destination = destPath,
                    onProgress = { progress ->
                        downloadProgress = progress
                    }
                )
            }
            dbRepo.setModelDownloaded(true)
            dbRepo.setModelPath(destPath)
            status = ModelStatus.Downloaded
        } catch (e: Exception) {
            status = ModelStatus.Error
            errorMessage = e.message ?: "Download failed"
        }
    }

    fun markReady() {
        status = ModelStatus.Ready
    }

    fun markLoadingIntoMemory() {
        status = ModelStatus.LoadingIntoMemory
    }
}
