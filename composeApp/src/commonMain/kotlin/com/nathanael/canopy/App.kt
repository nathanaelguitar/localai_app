package com.nathanael.canopy

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nathanael.canopy.model.Screen
import com.nathanael.canopy.state.CanopyState
import com.nathanael.canopy.theme.CanopyTheme
import com.nathanael.canopy.ui.ChatScreen
import com.nathanael.canopy.ui.HomeScreen
import com.nathanael.canopy.ui.NoticeBanner
import com.nathanael.canopy.ui.OakBackground
import com.nathanael.canopy.ui.OnboardingScreen
import com.nathanael.canopy.ui.SettingsScreen
import com.nathanael.canopy.ui.WorkflowsScreen

@Composable
fun App() {
    val state = remember { CanopyState() }

    CanopyTheme(dark = state.isDark) {
        OakBackground(dark = state.isDark) {
            Box(
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .windowInsetsPadding(WindowInsets.ime)
            ) {
                when (state.screen) {
                    Screen.Onboarding -> OnboardingScreen(state)
                    Screen.Home -> HomeScreen(state)
                    Screen.Chat -> ChatScreen(state)
                    Screen.Workflows -> WorkflowsScreen(state)
                    Screen.Settings -> SettingsScreen(state)
                }
                Box(Modifier.align(Alignment.TopCenter)) {
                    NoticeBanner(state.notice, state::dismissNotice)
                }
            }
        }
    }
}
