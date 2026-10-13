package dev.watchstore.data.remote

import dev.watchstore.data.model.Finish
import dev.watchstore.data.model.Hero
import dev.watchstore.data.model.Highlight
import dev.watchstore.data.model.HighlightIcon
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.ProductKind

/**
 * DTO -> domain mapping.
 *
 * Every enum is parsed defensively. A server that ships a value this build has
 * never heard of must not crash the app; it degrades to a default and the
 * catalogue still renders.
 */

private inline fun <reified T : Enum<T>> String.toEnumOr(default: T): T =
    enumValues<T>().firstOrNull { it.name.equals(this, ignoreCase = true) } ?: default

fun ProductDto.toDomain(): Product = Product(
    id = id,
    kind = kind.toEnumOr(ProductKind.WATCH),
    name = name,
    brand = brand,
    tagline = tagline,
    description = description,
    priceCents = priceCents,
    finishes = finishes.map {
        Finish(
            id = it.name,
            name = it.name,
            tone = it.tone.toEnumOr(dev.watchstore.data.model.FinishTone.Obsidian),
        )
    },
    highlights = highlights.map {
        Highlight(
            icon = it.icon.toEnumOr(HighlightIcon.Craft),
            title = it.title,
            caption = it.caption,
        )
    },
    pairsWithId = pairsWithId,
    rating = rating,
    reviewCount = reviewCount,
    isFeatured = isFeatured,
    isPopular = isPopular,
    artSeed = artSeed,
    // Artwork is keyed by product id locally; a remote catalogue reuses the
    // same generated assets rather than shipping image URLs.
    gallery = dev.watchstore.data.catalog.galleryFor(id),
)

fun HeroDto.toDomain(): Hero = Hero(
    id = id,
    eyebrow = eyebrow,
    title = title,
    subtitle = subtitle,
    cta = cta,
    productId = productId,
    targetKind = targetKind.toEnumOr(ProductKind.WATCH),
)

fun CatalogResponse.toDomain(): Pair<List<Product>, List<Hero>> =
    products.map { it.toDomain() } to heroes.map { it.toDomain() }