package com.nathanael.canopy

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeUIViewController
import com.nathanael.canopy.db.DriverFactory
import com.nathanael.canopy.platform.PlatformPaths

@OptIn(ExperimentalComposeUiApi::class)
fun MainViewController() = ComposeUIViewController(
    configure = {
        parallelRendering = false
        opaque = false
    }
) {
    App(
        driverFactory = DriverFactory(),
        platformPaths = PlatformPaths()
    )
}
