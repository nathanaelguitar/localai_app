package com.nathanael.canopy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nathanael.canopy.data.ModelStatus
import com.nathanael.canopy.model.Screen
import com.nathanael.canopy.state.CanopyState

@Composable
fun SettingsScreen(state: CanopyState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SectionTitle("Settings")
        Spacer(Modifier.height(14.dp))

        GlassPanel(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Dark oak theme", fontWeight = FontWeight.Bold)
                    Text("Toggle appearance mode", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = state.isDark, onCheckedChange = { state.isDark = it })
            }
        }

        Spacer(Modifier.height(12.dp))
        GlassPanel(Modifier.fillMaxWidth()) {
            Text("AI Model", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            val mm = state.modelManager
            if (mm != null) {
                val statusLabel = when (mm.status) {
                    ModelStatus.NotDownloaded -> "Not downloaded"
                    ModelStatus.Downloading -> "Downloading..."
                    ModelStatus.Downloaded -> "Downloaded - ready to use"
                    ModelStatus.LoadingIntoMemory -> "Loading into memory..."
                    ModelStatus.Ready -> "Ready"
                    ModelStatus.Error -> "Error: ${mm.errorMessage}"
                }
                Text(
                    "Gemma 4 E2B (on-device)",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Status: $statusLabel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (mm.status == ModelStatus.Downloading) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { mm.downloadProgress },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (mm.status == ModelStatus.NotDownloaded || mm.status == ModelStatus.Error) {
                    Spacer(Modifier.height(8.dp))
                    PremiumButton("Download Model", onClick = {
                        state.screen = Screen.ModelDownload
                    })
                }
            } else {
                Text(
                    "Demo mode - no model loaded",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        GlassPanel(Modifier.fillMaxWidth()) {
            Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Canopy runs AI inference entirely on your device using llama.cpp. Your conversations are stored locally in SQLite. No data is sent to external servers.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.weight(1f))
        AppBottomBar(
            current = Screen.Settings,
            onHome = state::goHome,
            onWorkflows = state::goWorkflows,
            onSettings = state::goSettings
        )
    }
}
