package dev.watchstore.data.catalog

import dev.watchstore.data.model.Finish
import dev.watchstore.data.model.FinishTone
import dev.watchstore.data.model.Hero
import dev.watchstore.data.model.Highlight
import dev.watchstore.data.model.HighlightIcon
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.ProductKind

/**
 * Seed catalogue.
 *
 * v1 ships the data in Kotlin rather than a JSON resource: it is type-checked at
 * compile time, needs no file I/O (so it works identically on every target), and
 * stays diff-friendly in review. `ProductRepository` is the seam — swapping this
 * for a network source later touches nothing above the data layer.
 */

private fun finishes(vararg pairs: Pair<FinishTone, String>) =
    pairs.mapIndexed { i, (tone, name) -> Finish(id = "f$i", name = name, tone = tone) }

private const val BOX_INTERIOR = "Soft velvet interior"
private const val BOX_LOCK = "Secure brass lock"
private const val BOX_WOOD = "Hand-finished hardwood"

private val watchHighlights = listOf(
    Highlight(HighlightIcon.Sapphire, "Sapphire crystal", "Scratch resistant"),
    Highlight(HighlightIcon.Movement, "Automatic movement", "Self-winding calibre"),
    Highlight(HighlightIcon.Water, "Water resistant", "300 m / 1000 ft"),
)

private fun watch(
    id: String,
    name: String,
    brand: String,
    tagline: String,
    price: Long,
    tones: List<Finish>,
    pairsWith: String,
    rating: Double,
    reviews: Int,
    seed: Int,
    featured: Boolean = false,
    popular: Boolean = false,
    description: String = "",
) = Product(
    id = id,
    kind = ProductKind.WATCH,
    name = name,
    brand = brand,
    tagline = tagline,
    description = description.ifEmpty { "$brand $name. $tagline. Crafted in small runs and finished by hand." },
    priceCents = price,
    finishes = tones,
    highlights = watchHighlights,
    pairsWithId = pairsWith,
    rating = rating,
    reviewCount = reviews,
    isFeatured = featured,
    isPopular = popular,
    artSeed = seed,
    gallery = galleryFor(id),
)

private fun watchBox(
    id: String,
    name: String,
    tagline: String,
    price: Long,
    tones: List<Finish>,
    rating: Double,
    reviews: Int,
    seed: Int,
    featured: Boolean = false,
    popular: Boolean = false,
    slots: Int = 2,
) = Product(
    id = id,
    kind = ProductKind.BOX,
    name = name,
    brand = "Atelier Case",
    tagline = tagline,
    description = "Holds $slots watches, with a hand-finished hardwood body, " +
        "a soft velvet interior and a secure brass lock.",
    priceCents = price,
    finishes = tones,
    highlights = listOf(
        Highlight(HighlightIcon.Wood, BOX_WOOD, "Solid, not veneer"),
        Highlight(HighlightIcon.Interior, BOX_INTERIOR, "Protects your pieces"),
        Highlight(HighlightIcon.Lock, BOX_LOCK, "Key included"),
    ),
    rating = rating,
    reviewCount = reviews,
    isFeatured = featured,
    isPopular = popular,
    artSeed = seed,
    gallery = galleryFor(id),
)

private fun accessory(
    id: String,
    name: String,
    tagline: String,
    price: Long,
    tone: FinishTone,
    seed: Int,
    icon: HighlightIcon,
    title: String,
    caption: String,
) = Product(
    id = id,
    kind = ProductKind.ACCESSORY,
    name = name,
    brand = "Atelier Case",
    tagline = tagline,
    description = tagline,
    priceCents = price,
    finishes = listOf(Finish("f0", tone.displayName, tone)),
    highlights = listOf(
        Highlight(icon, title, caption),
        Highlight(HighlightIcon.Craft, "Made to last", "Serviceable parts"),
        Highlight(HighlightIcon.Warranty, "2 year cover", "Included"),
    ),
    rating = 4.4,
    reviewCount = 60 + seed,
    artSeed = seed,
    gallery = galleryFor(id),
)

val seedProducts: List<Product> = listOf(
    // ---- Watches ------------------------------------------------------------
    watch(
        id = "meridian-38", name = "Meridian 38", brand = "Halcyon",
        tagline = "Steel automatic with a slate dial", price = 124_000,
        tones = finishes(FinishTone.Obsidian to "Obsidian", FinishTone.Steel to "Steel"),
        pairsWith = "obsidian-black", rating = 4.8, reviews = 214, seed = 1,
        featured = true, popular = true,
    ),
    watch(
        id = "solstice-diver", name = "Solstice Diver 300", brand = "Halcyon",
        tagline = "Deep green bezel, screw-down crown", price = 89_000,
        tones = finishes(FinishTone.Emerald to "Emerald", FinishTone.Midnight to "Midnight"),
        pairsWith = "emerald-hardshell", rating = 4.7, reviews = 341, seed = 2,
        featured = true, popular = true,
    ),
    watch(
        id = "onyx-chrono", name = "Onyx Chronograph 41", brand = "Vellum",
        tagline = "Three registers, flyback lever", price = 168_000,
        tones = finishes(FinishTone.Obsidian to "Obsidian"),
        pairsWith = "basalt-compact", rating = 4.9, reviews = 128, seed = 3,
        featured = true,
    ),
    watch(
        id = "aurum-field", name = "Aurum Field 36", brand = "Vellum",
        tagline = "Champagne dial on a brushed bracelet", price = 72_000,
        tones = finishes(FinishTone.Champagne to "Champagne", FinishTone.Sand to "Sand"),
        pairsWith = "sand-linen", rating = 4.5, reviews = 402, seed = 4,
        popular = true,
    ),
    watch(
        id = "nocturne-moon", name = "Nocturne Moonphase 39", brand = "Aureus",
        tagline = "Aventine moonphase, blued hands", price = 245_000,
        tones = finishes(FinishTone.Midnight to "Midnight", FinishTone.Bordeaux to "Bordeaux"),
        pairsWith = "bordeaux-presentation", rating = 5.0, reviews = 76, seed = 5,
        featured = true,
    ),
    watch(
        id = "terrane-gmt", name = "Terrane GMT 40", brand = "Aureus",
        tagline = "Cognac strap, jumping local hour", price = 112_000,
        tones = finishes(FinishTone.Cognac to "Cognac", FinishTone.Walnut to "Walnut"),
        pairsWith = "cognac-roll", rating = 4.6, reviews = 189, seed = 6,
    ),
    watch(
        id = "halcyon-34", name = "Halcyon 34", brand = "Halcyon",
        tagline = "The smallest case we still make", price = 64_000,
        tones = finishes(FinishTone.Oyster to "Oyster", FinishTone.Sand to "Sand"),
        pairsWith = "oyster-roll", rating = 4.4, reviews = 511, seed = 7,
        popular = true,
    ),
    watch(
        id = "basalt-auto", name = "Basalt Auto 38", brand = "Vellum",
        tagline = "Matte black, no shine anywhere", price = 98_000,
        tones = finishes(FinishTone.Obsidian to "Obsidian", FinishTone.Bordeaux to "Bordeaux"),
        pairsWith = "basalt-compact", rating = 4.7, reviews = 233, seed = 8,
    ),
    watch(
        id = "cascade-diver", name = "Cascade Diver 42", brand = "Halcyon",
        tagline = "Oversized case, oversized lume", price = 134_000,
        tones = finishes(FinishTone.Emerald to "Emerald", FinishTone.Oyster to "Oyster"),
        pairsWith = "emerald-hardshell", rating = 4.8, reviews = 297, seed = 9,
        popular = true,
    ),
    watch(
        id = "vesper-slim", name = "Vesper Slim 33", brand = "Aureus",
        tagline = "Seven millimetres, nothing spare", price = 58_000,
        tones = finishes(FinishTone.Oyster to "Oyster"),
        pairsWith = "sand-linen", rating = 4.3, reviews = 620, seed = 10,
    ),

    // ---- Boxes & cases ------------------------------------------------------
    watchBox(
        id = "walnut-prestige", name = "Walnut Prestige Box", tagline = "Six-watch dresser",
        price = 15_900, tones = finishes(FinishTone.Walnut to "Walnut"),
        rating = 4.9, reviews = 88, seed = 21, featured = true, slots = 6,
    ),
    watchBox(
        id = "midnight-blue", name = "Midnight Blue Travel Case", tagline = "Pocket-sized, hard shell",
        price = 12_900, tones = finishes(FinishTone.Midnight to "Midnight"),
        rating = 4.6, reviews = 143, seed = 22, popular = true, slots = 3,
    ),
    watchBox(
        id = "obsidian-black", name = "Obsidian Black Case", tagline = "The matte standard",
        price = 14_900, tones = finishes(FinishTone.Obsidian to "Obsidian"),
        rating = 4.8, reviews = 205, seed = 23, popular = true, slots = 4,
    ),
    watchBox(
        id = "sand-linen", name = "Sand Linen Folio", tagline = "Soft, flat, for travel",
        price = 8_900, tones = finishes(FinishTone.Sand to "Sand"),
        rating = 4.4, reviews = 176, seed = 24, slots = 2,
    ),
    watchBox(
        id = "emerald-hardshell", name = "Emerald Hardshell", tagline = "Waterproof, impact tested",
        price = 17_900, tones = finishes(FinishTone.Emerald to "Emerald"),
        rating = 4.9, reviews = 97, seed = 25, featured = true, slots = 4,
    ),
    watchBox(
        id = "cognac-roll", name = "Cognac Leather Roll", tagline = "Rolls shut, ages well",
        price = 11_900, tones = finishes(FinishTone.Cognac to "Cognac"),
        rating = 4.7, reviews = 134, seed = 26, slots = 3,
    ),
    watchBox(
        id = "oyster-roll", name = "Oyster Travel Roll", tagline = "The lightest one we make",
        price = 13_900, tones = finishes(FinishTone.Oyster to "Oyster"),
        rating = 4.5, reviews = 121, seed = 27, popular = true, slots = 4,
    ),
    watchBox(
        id = "bordeaux-presentation", name = "Bordeaux Presentation Box", tagline = "Oiled leather, brass hinge",
        price = 19_900, tones = finishes(FinishTone.Bordeaux to "Bordeaux"),
        rating = 4.8, reviews = 64, seed = 28, slots = 6,
    ),
    watchBox(
        id = "basalt-compact", name = "Basalt Compact Case", tagline = "Two watches, no bulk",
        price = 9_900, tones = finishes(FinishTone.Obsidian to "Obsidian"),
        rating = 4.3, reviews = 152, seed = 29, slots = 2,
    ),
    watchBox(
        id = "cascade-trunk", name = "Cascade Steamer Trunk", tagline = "Six, and room to grow",
        price = 24_900, tones = finishes(FinishTone.Walnut to "Walnut", FinishTone.Steel to "Steel"),
        rating = 5.0, reviews = 41, seed = 30, featured = true, slots = 6,
    ),

    // ---- Accessories --------------------------------------------------------
    accessory(
        id = "velvet-cushions", name = "Velvet Cushion Set", tagline = "Six replacement cushions",
        price = 3_900, tone = FinishTone.Bordeaux, seed = 41,
        icon = HighlightIcon.Interior, title = "Matches our liners", caption = "Drop-in fit",
    ),
    accessory(
        id = "spring-bar-tool", name = "Spring Bar Tool", tagline = "Two tips, one purpose",
        price = 2_400, tone = FinishTone.Steel, seed = 42,
        icon = HighlightIcon.Craft, title = "Hardened steel", caption = "Won't slip",
    ),
    accessory(
        id = "leather-strap", name = "Cognac Leather Strap", tagline = "Quick-release, 20mm",
        price = 7_900, tone = FinishTone.Cognac, seed = 43,
        icon = HighlightIcon.Craft, title = "Vegetable tanned", caption = "Develops patina",
    ),
    accessory(
        id = "steel-bracelet", name = "Brushed Steel Bracelet", tagline = "Fits all our 38-42mm cases",
        price = 12_900, tone = FinishTone.Steel, seed = 44,
        icon = HighlightIcon.Craft, title = "Solid links", caption = "No stretch",
    ),
)

val seedHeroes: List<Hero> = listOf(
    Hero(
        id = "hero-craft", eyebrow = "Autumn collection",
        title = "Built to\noutlive us",
        subtitle = "Small-run cases in walnut, oak and painted steel.",
        cta = "Shop Now", productId = "cascade-trunk",
        targetKind = ProductKind.BOX,
    ),
    Hero(
        id = "hero-diver", eyebrow = "New this season",
        title = "Green\nand deep",
        subtitle = "The Solstice Diver 300, back in emerald.",
        cta = "View now", productId = "solstice-diver",
        targetKind = ProductKind.WATCH,
    ),
    Hero(
        id = "hero-rollers", eyebrow = "Best sellers",
        title = "The roll\nthat started it",
        subtitle = "Soft, compact, and endlessly re-lined.",
        cta = "See best sellers", productId = "oyster-roll",
        targetKind = ProductKind.BOX,
    ),
)