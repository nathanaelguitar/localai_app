package com.nathanael.canopy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nathanael.canopy.db.DriverFactory
import com.nathanael.canopy.platform.PlatformPaths

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(
                driverFactory = DriverFactory(this),
                platformPaths = PlatformPaths(this)
            )
        }
    }
}
