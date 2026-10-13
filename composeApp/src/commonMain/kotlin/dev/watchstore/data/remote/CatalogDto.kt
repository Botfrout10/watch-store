package dev.watchstore.data.remote

import kotlinx.serialization.Serializable

/**
 * Wire format for `GET /api/v1/catalog`.
 *
 * Deliberately decoupled from the domain model:
 *  - enums travel as **strings** (`kind`, `tone`, `icon`) so a new enum value on
 *    the server cannot crash an older client
 *  - unknown values degrade to a sensible default rather than throwing
 *  - adding an optional field does not break older clients
 *
 * See `docs/api.md` for the contract.
 */

@Serializable
data class CatalogResponse(
    val products: List<ProductDto> = emptyList(),
    val heroes: List<HeroDto> = emptyList(),
)

@Serializable
data class ProductDto(
    val id: String,
    val kind: String = "WATCH",
    val name: String = "",
    val brand: String = "",
    val tagline: String = "",
    val description: String = "",
    val priceCents: Long = 0,
    val finishes: List<FinishDto> = emptyList(),
    val highlights: List<HighlightDto> = emptyList(),
    val pairsWithId: String? = null,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val isFeatured: Boolean = false,
    val isPopular: Boolean = false,
    val artSeed: Int = 0,
)

@Serializable
data class FinishDto(
    val name: String = "",
    /** Must match a `FinishTone` name; unknown tones fall back to Obsidian. */
    val tone: String = "Obsidian",
)

@Serializable
data class HighlightDto(
    val icon: String = "Craft",
    val title: String = "",
    val caption: String = "",
)

@Serializable
data class HeroDto(
    val id: String = "",
    val eyebrow: String = "",
    val title: String = "",
    val subtitle: String = "",
    val cta: String = "",
    val productId: String = "",
    val targetKind: String = "WATCH",
)