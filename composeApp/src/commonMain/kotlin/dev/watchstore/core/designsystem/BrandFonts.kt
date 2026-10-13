package dev.watchstore.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The two brand faces, resolved once at theme time.
 *
 * `display` carries every serif role — product names, prices, headings.
 * `ui` carries labels, body copy and anything functional.
 */
data class BrandFonts(
    val display: FontFamily,
    val ui: FontFamily,
)

/**
 * Platform families, used when bundled fonts are unavailable.
 *
 * Noto Serif + Roboto on Android, New York + SF Pro on iOS. Both are well-hinted
 * editorial pairings, so this degrades cleanly rather than breaking.
 */
val FallbackBrandFonts = BrandFonts(
    display = FontFamily.Serif,
    ui = FontFamily.SansSerif,
)

val LocalBrandFonts = staticCompositionLocalOf { FallbackBrandFonts }

/** The serif face for display and price roles. */
@Composable
fun brandDisplay(): FontFamily = LocalBrandFonts.current.display

/** The sans face for labels and body roles. */
@Composable
fun brandUi(): FontFamily = LocalBrandFonts.current.ui

/** Price style — serif, gold at call sites, tight tracking. */
@Composable
fun brandPriceStyle(): TextStyle = TextStyle(
    fontFamily = brandDisplay(),
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 24.sp,
    letterSpacing = (-0.2).sp,
)

/**
 * Loads the bundled brand faces.
 *
 * Compose Multiplatform 1.12.1 exposes no `Font(FontResource)` overload, so the
 * bundled variable TTFs cannot be loaded from `commonMain`. Only this factory is
 * `expect`/`actual`; everything else — the type scale, the roles, the call sites —
 * stays shared. When CMP gains the overload, replace the actuals with a direct
 * `Font(Res.font.Inter)` call and delete this file.
 */
expect fun loadBrandFonts(): BrandFonts