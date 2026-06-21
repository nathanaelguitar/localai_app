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
import com.nathanael.canopy.data.DatabaseRepository
import com.nathanael.canopy.data.DemoChatRepository
import com.nathanael.canopy.data.LlamaCppChatRepository
import com.nathanael.canopy.data.ModelManager
import com.nathanael.canopy.data.ModelStatus
import com.nathanael.canopy.db.CanopyDatabase
import com.nathanael.canopy.db.DriverFactory
import com.nathanael.canopy.model.Screen
import com.nathanael.canopy.platform.PlatformPaths
import com.nathanael.canopy.state.CanopyState
import com.nathanael.canopy.theme.CanopyTheme
import com.nathanael.canopy.ui.ChatScreen
import com.nathanael.canopy.ui.HomeScreen
import com.nathanael.canopy.ui.ModelDownloadScreen
import com.nathanael.canopy.ui.NoticeBanner
import com.nathanael.canopy.ui.OakBackground
import com.nathanael.canopy.ui.OnboardingScreen
import com.nathanael.canopy.ui.SettingsScreen
import com.nathanael.canopy.ui.WorkflowsScreen

@Composable
fun App(
    driverFactory: DriverFactory? = null,
    platformPaths: PlatformPaths? = null
) {
    val state = remember {
        try {
            if (driverFactory != null && platformPaths != null) {
                val database = CanopyDatabase(driverFactory.createDriver())
                val dbRepo = DatabaseRepository(database)
                val modelManager = ModelManager(platformPaths, dbRepo)
                modelManager.checkModelStatus()
                val chatRepo = if (modelManager.status == ModelStatus.Downloaded ||
                    modelManager.status == ModelStatus.Ready
                ) {
                    LlamaCppChatRepository(modelManager)
                } else {
                    DemoChatRepository()
                }
                CanopyState(
                    repository = chatRepo,
                    dbRepo = dbRepo,
                    modelManager = modelManager
                )
            } else {
                CanopyState()
            }
        } catch (_: Exception) {
            CanopyState()
        }
    }

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
                    Screen.ModelDownload -> ModelDownloadScreen(state)
                    Screen.Home -> HomeScreen(state)
                    Screen.Chat -> ChatScreen(state)
                    Screen.Workflows -> WorkflowsScreen(state)
                    Screen.Settings -> SettingsScreen(state)
                }
                if (state.notice.isNotBlank()) {
                    Box(Modifier.align(Alignment.TopCenter)) {
                        NoticeBanner(state.notice, state::dismissNotice)
                    }
                }
            }
        }
    }
}
