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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nathanael.canopy.state.CanopyState

@Composable
fun OnboardingScreen(state: CanopyState) {
    val pages = listOf(
        Triple("Canopy", "AI workspace with an oak-rooted visual identity.", "Not a generic chatbot clone. It is organized around workspaces, workflows, and premium product texture."),
        Triple("Work in branches", "Separate deals, finance research, and career prep.", "Each workspace has pinned threads, saved workflows, and its own context surface."),
        Triple("Private AI", "Runs on-device with Gemma 4 E2B.", "Your data never leaves the device. Download the model once and use Canopy anywhere, with or without internet.")
    )
    val page = pages[state.onboardingStep]

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        GlassPanel(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandMark()
                Column(Modifier.padding(start = 14.dp)) {
                    Text(page.first, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text(page.second, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(28.dp))
            Text(page.third, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(28.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${state.onboardingStep + 1} / ${pages.size}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (state.onboardingStep > 0) SecondaryButton("Back", state::previousOnboardingStep)
                    PremiumButton(if (state.onboardingStep == pages.lastIndex) "Enter Canopy" else "Next", state::nextOnboardingStep)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = state::goHome, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Skip setup")
        }
    }
}
