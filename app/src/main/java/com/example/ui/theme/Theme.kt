package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = HarvestGreen80,
    onPrimary = HarvestGreenDark,
    primaryContainer = HarvestGreenDark,
    onPrimaryContainer = HarvestGreen80,
    secondary = HarvestAmber80,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF452B00),
    onSecondaryContainer = HarvestAmber80,
    tertiary = HarvestMint80,
    background = BackgroundDark,
    onBackground = Color(0xFFE2EBE5),
    surface = SurfaceDark,
    onSurface = Color(0xFFE2EBE5),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFB5C9BD)
)

private val LightColorScheme = lightColorScheme(
    primary = HarvestGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = HarvestMintContainer,
    onPrimaryContainer = HarvestGreenDark,
    secondary = HarvestAmberAccent,
    onSecondary = Color.White,
    secondaryContainer = HarvestAmberContainer,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = FertileSoil,
    background = BackgroundLight,
    onBackground = Color(0xFF14241B),
    surface = SurfaceLight,
    onSurface = Color(0xFF14241B),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF3F5447)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent rich organic branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
