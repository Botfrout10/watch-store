package dev.watchstore.data.model

import androidx.compose.ui.graphics.Color

enum class ProductKind(val label: String) {
    WATCH("Watches"),
    BOX("Boxes"),
    ACCESSORY("Accessories"),
}

/**
 * Product finishes are a closed set rather than free-form hex so that swatches,
 * placeholder gradients and (later) generated imagery all derive from one source
 * of truth.
 */
enum class FinishTone(val argb: Long, val displayName: String) {
    Obsidian(0xFF1B1B1D, "Obsidian"),
    Walnut(0xFF6B4526, "Walnut"),
    Sand(0xFFD8C4A0, "Sand"),
    Emerald(0xFF1F4D3D, "Emerald"),
    Midnight(0xFF1B2A4A, "Midnight"),
    Cognac(0xFF8A4B22, "Cognac"),
    Oyster(0xFFB8B2A6, "Oyster"),
    Bordeaux(0xFF5C1F2B, "Bordeaux"),
    Steel(0xFF9AA0A6, "Steel"),
    Champagne(0xFFD9C08A, "Champagne"),
}

fun FinishTone.color(): Color = Color(argb)

data class Finish(
    val id: String,
    val name: String,
    val tone: FinishTone,
)

/** Small icon + caption pair, rendered as the three-up strip on the detail page. */
enum class HighlightIcon { Wood, Interior, Lock, Sapphire, Movement, Water, Craft, Warranty }

data class Highlight(
    val icon: HighlightIcon,
    val title: String,
    val caption: String,
)

data class Product(
    val id: String,
    val kind: ProductKind,
    val name: String,
    val brand: String,
    val tagline: String,
    val description: String,
    val priceCents: Long,
    val finishes: List<Finish>,
    val highlights: List<Highlight>,
    /** Id of the complementary product (watch <-> case) for "Complete the set". */
    val pairsWithId: String? = null,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val isFeatured: Boolean = false,
    val isPopular: Boolean = false,
    /** Stable seed so the placeholder gradient and later generated image match. */
    val artSeed: Int = 0,
    /**
     * Artwork gallery — one shot per finish, so picking a colour swatch changes
     * the photograph. Empty falls back to ProductImage's drawn silhouette.
     */
    val gallery: List<org.jetbrains.compose.resources.DrawableResource> = emptyList(),
)

data class Hero(
    val id: String,
    val eyebrow: String,
    val title: String,
    val subtitle: String,
    val cta: String,
    /** Product shown in the hero panel. */
    val productId: String,
    val targetKind: ProductKind,
)

data class CartLine(val product: Product, val quantity: Int)

/** $1,240.00 — always two decimals so the price column stays aligned. */
fun formatPrice(cents: Long): String {
    val dollars = cents / 100
    val remainder = cents % 100
    return "$" + groupThousands(dollars) + "." + remainder.toString().padStart(2, '0')
}

private fun groupThousands(value: Long): String {
    val s = value.toString()
    if (s.length <= 3) return s
    val sb = StringBuilder()
    var count = 0
    for (i in s.indices.reversed()) {
        sb.append(s[i])
        count++
        if (count % 3 == 0 && i != 0) sb.append(',')
    }
    return sb.reverse().toString()
}