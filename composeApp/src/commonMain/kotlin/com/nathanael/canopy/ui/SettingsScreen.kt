package com.nathanael.canopy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nathanael.canopy.model.Screen
import com.nathanael.canopy.state.CanopyState

@Composable
fun SettingsScreen(state: CanopyState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SectionTitle("Settings", "Local controls now. Backend integration later.")
        Spacer(Modifier.height(14.dp))

        GlassPanel(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Premium dark oak theme", fontWeight = FontWeight.Bold)
                    Text("Toggle proves settings state is wired.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = state.isDark, onCheckedChange = { state.isDark = it })
            }
        }

        Spacer(Modifier.height(12.dp))
        GlassPanel(Modifier.fillMaxWidth()) {
            Text("Backend provider", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = state.providerName,
                onValueChange = { state.providerName = it },
                label = { Text("Provider name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.endpoint,
                onValueChange = { state.endpoint = it },
                label = { Text("Chat endpoint") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.apiKeyLabel,
                onValueChange = { state.apiKeyLabel = it },
                label = { Text("API key label") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton("Test", onClick = state::testConnection)
                PremiumButton("Save", onClick = state::saveSettings)
            }
        }

        Spacer(Modifier.height(12.dp))
        GlassPanel(Modifier.fillMaxWidth()) {
            Text("Review posture", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Canopy has its own name, visual system, onboarding, workspaces, workflows, and settings. Keep that direction when adding your backend so the product does not look like a generic AI wrapper.",
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
