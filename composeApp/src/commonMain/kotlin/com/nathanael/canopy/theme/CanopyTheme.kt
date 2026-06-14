package com.nathanael.canopy.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object CanopyColors {
    val Bark = Color(0xFF6E4B2F)
    val BarkDark = Color(0xFF2B1B12)
    val Gold = Color(0xFFD7B46A)
    val Moss = Color(0xFF54745A)
    val MossDark = Color(0xFF1E3328)
    val Cream = Color(0xFFF5EBDD)
    val Ink = Color(0xFF15120F)
    val Night = Color(0xFF080A08)
    val GlassDark = Color(0xCC141812)
    val GlassLight = Color(0xDDF8EFE3)
}

@Composable
fun CanopyTheme(
    dark: Boolean,
    content: @Composable () -> Unit
) {
    val scheme = if (dark) {
        darkColorScheme(
            primary = CanopyColors.Gold,
            onPrimary = CanopyColors.Ink,
            secondary = Color(0xFF9DB68D),
            onSecondary = CanopyColors.Ink,
            background = CanopyColors.Night,
            onBackground = CanopyColors.Cream,
            surface = Color(0xFF11140F),
            onSurface = CanopyColors.Cream,
            surfaceVariant = Color(0xFF1F271F),
            onSurfaceVariant = Color(0xFFE4D4BF),
            outline = Color(0xFF7B6A51)
        )
    } else {
        lightColorScheme(
            primary = CanopyColors.Bark,
            onPrimary = CanopyColors.Cream,
            secondary = CanopyColors.Moss,
            onSecondary = Color.White,
            background = Color(0xFFFFFAF3),
            onBackground = CanopyColors.Ink,
            surface = Color(0xFFFFF8EC),
            onSurface = CanopyColors.Ink,
            surfaceVariant = Color(0xFFE9D9C2),
            onSurfaceVariant = Color(0xFF3A3026),
            outline = Color(0xFF9A815B)
        )
    }

    MaterialTheme(
        colorScheme = scheme,
        content = content
    )
}
