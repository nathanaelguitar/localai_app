package com.nathanael.canopy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nathanael.canopy.data.ModelStatus
import com.nathanael.canopy.state.CanopyState
import kotlinx.coroutines.launch

@Composable
fun ModelDownloadScreen(state: CanopyState) {
    val modelManager = state.modelManager ?: return
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlassPanel(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandMark()
                Column(Modifier.padding(start = 14.dp)) {
                    Text(
                        "Download AI Model",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Gemma 4 E2B - runs fully on device",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(20.dp))

            when (modelManager.status) {
                ModelStatus.NotDownloaded -> {
                    Text(
                        "Canopy uses a local AI model for private, offline inference. No data leaves your device.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Download size: ~3 GB",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(20.dp))
                    PremiumButton(
                        "Download Model",
                        onClick = { scope.launch { modelManager.downloadModel() } },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                ModelStatus.Downloading -> {
                    Text(
                        "Downloading model...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { modelManager.downloadProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${(modelManager.downloadProgress * 100).toInt()}%",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                ModelStatus.Downloaded, ModelStatus.Ready -> {
                    Text(
                        "Model ready. Tap continue to start using Canopy with local AI.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(20.dp))
                    PremiumButton(
                        "Continue",
                        onClick = { state.goHome() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                ModelStatus.Error -> {
                    Text(
                        "Download failed: ${modelManager.errorMessage}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    PremiumButton(
                        "Retry",
                        onClick = { scope.launch { modelManager.downloadModel() } },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                ModelStatus.LoadingIntoMemory -> {
                    Text(
                        "Loading model into memory...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    CircularProgressIndicator()
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { state.goHome() }) {
            Text("Skip - use demo mode")
        }
    }
}
