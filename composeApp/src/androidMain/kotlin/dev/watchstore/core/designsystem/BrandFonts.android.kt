package dev.watchstore.core.designsystem

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import dev.watchstore.platform.ShareBridge

/**
 * Compose Resources packs everything under `composeResources/` into the APK's
 * assets, namespaced by the resource package. This prefix must track
 * `packageOfResClass` in `composeApp/build.gradle.kts`.
 */
private const val FONT_ASSET_DIR = "composeResources/dev.watchstore.resources/font"

private const val DISPLAY_FONT = "$FONT_ASSET_DIR/PlayfairDisplay.ttf"
private const val UI_FONT = "$FONT_ASSET_DIR/Inter.ttf"

actual fun loadBrandFonts(): BrandFonts {
    val context = ShareBridge.context ?: return FallbackBrandFonts
    val assets = context.assets

    // Variable fonts: weight must be selected through variation settings or
    // every entry renders at the default instance.
    fun display(weight: Int) = Font(
        path = DISPLAY_FONT,
        assetManager = assets,
        weight = FontWeight(weight),
        variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
    )

    fun ui(weight: Int) = Font(
        path = UI_FONT,
        assetManager = assets,
        weight = FontWeight(weight),
        variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
    )

    return BrandFonts(
        display = FontFamily(display(400), display(500), display(600), display(700)),
        ui = FontFamily(ui(400), ui(500), ui(600), ui(700)),
    )
}