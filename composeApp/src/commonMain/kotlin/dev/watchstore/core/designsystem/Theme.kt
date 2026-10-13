package dev.watchstore.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = WatchStoreTypography,
        shapes = WatchStoreShapes,
        content = content,
    )
}