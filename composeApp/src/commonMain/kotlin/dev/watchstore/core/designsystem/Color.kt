package dev.watchstore.core.designsystem

import androidx.compose.ui.graphics.Color

/**
 * Brand: "golden sand on warm dark".
 *
 * The whole catalogue sits on warm, brown-shifted darks (never neutral grey or
 * green) with a single sand-gold accent. Prices and headlines are always gold or
 * cream so the eye has exactly one place to land.
 */

// ---- Dark scheme (default) -------------------------------------------------

val BaseDark = Color(0xFF14110D)
val SurfaceDark = Color(0xFF1E1A14)
val SurfaceHighDark = Color(0xFF272117)
val OutlineDark = Color(0xFF3A3227)

val Gold = Color(0xFFD8B26A)
val GoldSoft = Color(0xFFEBD3A3)
val OnGold = Color(0xFF2A1F0E)

val TextPrimaryDark = Color(0xFFF2EAD9)
val TextSecondaryDark = Color(0xFFA99C86)
val ErrorDark = Color(0xFFC4695E)

// ---- Light scheme (sand paper) ---------------------------------------------

val BaseLight = Color(0xFFF7F2E8)
val SurfaceLight = Color(0xFFFFFDF8)
val SurfaceHighLight = Color(0xFFF0E8D8)
val OutlineLight = Color(0xFFD9CDB6)

// On light, the raw gold fails contrast against cream, so the accent darkens.
val GoldLight = Color(0xFF9A7432)
val GoldSoftLight = Color(0xFFC9A863)
val OnGoldLight = Color(0xFFFFFFFF)

val TextPrimaryLight = Color(0xFF1F1A13)
val TextSecondaryLight = Color(0xFF6B6152)
val ErrorLight = Color(0xFFA63F34)

// ---- Product finish colours ------------------------------------------------
//
// Used by swatch selectors on the detail screen and by the generated imagery so
// that a product's visual identity is consistent between its card and its page.

object FinishColors {
    val Obsidian = Color(0xFF1B1B1D)
    val Walnut = Color(0xFF6B4526)
    val Sand = Color(0xFFD8C4A0)
    val Emerald = Color(0xFF1F4D3D)
    val Midnight = Color(0xFF1B2A4A)
    val Cognac = Color(0xFF8A4B22)
    val Oyster = Color(0xFFB8B2A6)
    val Bordeaux = Color(0xFF5C1F2B)
}