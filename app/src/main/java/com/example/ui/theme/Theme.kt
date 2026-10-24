package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val IndieForgeDarkColorScheme = darkColorScheme(
    primary = ElectricViolet,
    onPrimary = Color(0xFF120829),
    primaryContainer = DeepForgePurple,
    onPrimaryContainer = OnVioletContainer,
    secondary = CyberCyan,
    onSecondary = Color(0xFF042228),
    secondaryContainer = CyanContainer,
    onSecondaryContainer = OnCyanContainer,
    tertiary = EmeraldSuccess,
    onTertiary = Color(0xFF032319),
    background = ObsidianBackground,
    onBackground = CrispAlabaster,
    surface = ElevatedSlatePanel,
    onSurface = CrispAlabaster,
    surfaceVariant = CardSurface,
    onSurfaceVariant = MutedSteel,
    outline = HairlineBorder,
    outlineVariant = SubtleDivider,
    error = RoseDanger,
    onError = Color.White
)

val IndieForgeShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

@Composable
fun IndieForgeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IndieForgeDarkColorScheme,
        typography = Typography,
        shapes = IndieForgeShapes,
        content = content
    )
}
