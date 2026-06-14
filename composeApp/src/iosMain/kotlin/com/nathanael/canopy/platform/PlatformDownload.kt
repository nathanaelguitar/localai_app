package com.nathanael.canopy.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSURL
import platform.Foundation.NSURLResponse
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.dataTaskWithRequest
import platform.Foundation.writeToFile
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
actual suspend fun platformDownloadFile(
    url: String,
    destination: String,
    onProgress: (Float) -> Unit
) = suspendCancellableCoroutine { continuation ->
    val nsUrl = NSURL.URLWithString(url)
        ?: run {
            continuation.resumeWithException(IllegalArgumentException("Invalid URL: $url"))
            return@suspendCancellableCoroutine
        }

    val fileManager = NSFileManager.defaultManager

    // Remove existing file if present
    if (fileManager.fileExistsAtPath(destination)) {
        fileManager.removeItemAtPath(destination, null)
    }

    val request = NSMutableURLRequest(nsUrl)
    val session = NSURLSession.sessionWithConfiguration(
        NSURLSessionConfiguration.defaultSessionConfiguration
    )

    val task = session.dataTaskWithRequest(request) { data: NSData?, response: NSURLResponse?, error: NSError? ->
        if (error != null) {
            continuation.resumeWithException(
                Exception(error.localizedDescription ?: "Download failed")
            )
            return@dataTaskWithRequest
        }

        val httpResponse = response as? NSHTTPURLResponse
        if (httpResponse != null && httpResponse.statusCode !in 200L..299L) {
            continuation.resumeWithException(
                Exception("HTTP ${httpResponse.statusCode}")
            )
            return@dataTaskWithRequest
        }

        if (data != null) {
            val written = data.writeToFile(destination, atomically = true)
            if (written) {
                onProgress(1f)
                continuation.resume(Unit)
            } else {
                continuation.resumeWithException(Exception("Failed to write downloaded file"))
            }
        } else {
            continuation.resumeWithException(Exception("No data received"))
        }
    }

    task.resume()

    continuation.invokeOnCancellation {
        task.cancel()
    }
}
