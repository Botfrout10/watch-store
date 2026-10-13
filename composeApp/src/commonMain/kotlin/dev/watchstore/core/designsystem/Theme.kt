package dev.watchstore.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

private val DarkColors = darkColorScheme(
    primary = Gold,
    onPrimary = OnGold,
    primaryContainer = GoldSoft,
    onPrimaryContainer = OnGold,

    secondary = GoldSoft,
    onSecondary = OnGold,

    background = BaseDark,
    onBackground = TextPrimaryDark,

    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceHighDark,
    onSurfaceVariant = TextSecondaryDark,

    surfaceContainer = SurfaceDark,
    surfaceContainerHigh = SurfaceHighDark,
    surfaceContainerHighest = SurfaceHighDark,

    outline = OutlineDark,
    outlineVariant = OutlineDark,

    error = ErrorDark,
    onError = BaseDark,
)

private val LightColors = lightColorScheme(
    primary = GoldLight,
    onPrimary = OnGoldLight,
    primaryContainer = GoldSoftLight,
    onPrimaryContainer = BaseLight,

    secondary = GoldLight,
    onSecondary = OnGoldLight,

    background = BaseLight,
    onBackground = TextPrimaryLight,

    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceHighLight,
    onSurfaceVariant = TextSecondaryLight,

    surfaceContainer = SurfaceLight,
    surfaceContainerHigh = SurfaceHighLight,
    surfaceContainerHighest = SurfaceHighLight,

    outline = OutlineLight,
    outlineVariant = OutlineLight,

    error = ErrorLight,
    onError = SurfaceLight,
)

/**
 * The app is dark-first by design: the catalogue photography and gold accent are
 * tuned for it. Light is supported but secondary.
 */
@Composable
fun WatchStoreTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    // Resolved once per theme, then read through LocalBrandFonts by call sites.
    val fonts = remember { loadBrandFonts() }

    CompositionLocalProvider(LocalBrandFonts provides fonts) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = brandTypography(fonts.display, fonts.ui),
            shapes = WatchStoreShapes,
            content = content,
        )
    }
}