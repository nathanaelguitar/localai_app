package com.nathanael.canopy.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.nathanael.canopy.theme.CanopyColors

@Composable
fun OakBackground(
    dark: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    val base = if (dark) {
        Brush.verticalGradient(
            listOf(Color(0xFF070806), Color(0xFF11170F), Color(0xFF24170E))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFFFFFBF5), Color(0xFFF1E3CC), Color(0xFFD7C09A))
        )
    }

    Box(Modifier.fillMaxSize().background(base)) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val trunkColor = if (dark) CanopyColors.Bark.copy(alpha = 0.55f) else CanopyColors.Bark.copy(alpha = 0.28f)
            val branchColor = if (dark) Color(0xFFB88A4A).copy(alpha = 0.26f) else Color(0xFF6E4B2F).copy(alpha = 0.18f)
            val leafColor = if (dark) Color(0xFF6E9167).copy(alpha = 0.12f) else Color(0xFF54745A).copy(alpha = 0.11f)
            val goldMist = if (dark) CanopyColors.Gold.copy(alpha = 0.08f) else Color(0xFFFFE4A8).copy(alpha = 0.28f)

            drawCircle(goldMist, radius = w * 0.62f, center = Offset(w * 0.82f, h * 0.12f))
            drawCircle(leafColor, radius = w * 0.46f, center = Offset(w * 0.18f, h * 0.14f))
            drawCircle(leafColor, radius = w * 0.38f, center = Offset(w * 0.78f, h * 0.28f))
            drawCircle(leafColor.copy(alpha = leafColor.alpha * 0.7f), radius = w * 0.56f, center = Offset(w * 0.52f, h * 0.05f))

            val trunk = Path().apply {
                moveTo(w * 0.44f, h)
                cubicTo(w * 0.39f, h * 0.78f, w * 0.41f, h * 0.55f, w * 0.47f, h * 0.32f)
                cubicTo(w * 0.50f, h * 0.25f, w * 0.53f, h * 0.18f, w * 0.55f, h * 0.08f)
                lineTo(w * 0.62f, h * 0.09f)
                cubicTo(w * 0.59f, h * 0.27f, w * 0.57f, h * 0.49f, w * 0.61f, h)
                close()
            }
            drawPath(trunk, trunkColor)

            val stroke = Stroke(width = w * 0.018f, cap = StrokeCap.Round)
            drawLine(branchColor, Offset(w * 0.52f, h * 0.29f), Offset(w * 0.20f, h * 0.12f), strokeWidth = stroke.width, cap = StrokeCap.Round)
            drawLine(branchColor, Offset(w * 0.54f, h * 0.24f), Offset(w * 0.83f, h * 0.10f), strokeWidth = stroke.width * 0.8f, cap = StrokeCap.Round)
            drawLine(branchColor, Offset(w * 0.50f, h * 0.42f), Offset(w * 0.18f, h * 0.34f), strokeWidth = stroke.width * 0.65f, cap = StrokeCap.Round)
            drawLine(branchColor, Offset(w * 0.56f, h * 0.41f), Offset(w * 0.88f, h * 0.30f), strokeWidth = stroke.width * 0.65f, cap = StrokeCap.Round)
        }
        content()
    }
}
