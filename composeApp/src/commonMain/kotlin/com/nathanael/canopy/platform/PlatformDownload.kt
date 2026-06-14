package com.nathanael.canopy.platform

expect suspend fun platformDownloadFile(
    url: String,
    destination: String,
    onProgress: (Float) -> Unit
)
