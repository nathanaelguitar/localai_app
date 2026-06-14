package com.nathanael.canopy.platform

import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

actual suspend fun platformDownloadFile(
    url: String,
    destination: String,
    onProgress: (Float) -> Unit
) {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.connectTimeout = 30_000
    connection.readTimeout = 60_000
    connection.connect()

    val responseCode = connection.responseCode
    if (responseCode !in 200..299) {
        connection.disconnect()
        throw Exception("HTTP $responseCode")
    }

    val contentLength = connection.contentLengthLong
    var downloaded = 0L

    BufferedInputStream(connection.inputStream).use { input ->
        FileOutputStream(destination).use { output ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
                downloaded += bytesRead
                if (contentLength > 0) {
                    onProgress(downloaded.toFloat() / contentLength.toFloat())
                }
            }
        }
    }
    connection.disconnect()
    onProgress(1f)
}
