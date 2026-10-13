package dev.watchstore.data.catalog

import dev.watchstore.resources.Res
import dev.watchstore.resources.products_aurum_field
import dev.watchstore.resources.products_basalt_auto
import dev.watchstore.resources.products_basalt_compact
import dev.watchstore.resources.products_bordeaux_presentation
import dev.watchstore.resources.products_cascade_diver
import dev.watchstore.resources.products_cascade_trunk
import dev.watchstore.resources.products_cognac_roll
import dev.watchstore.resources.products_emerald_hardshell
import dev.watchstore.resources.products_halcyon_34
import dev.watchstore.resources.products_leather_strap
import dev.watchstore.resources.products_meridian_38
import dev.watchstore.resources.products_midnight_blue
import dev.watchstore.resources.products_nocturne_moon
import dev.watchstore.resources.products_obsidian_black
import dev.watchstore.resources.products_onyx_chrono
import dev.watchstore.resources.products_oyster_roll
import dev.watchstore.resources.products_sand_linen
import dev.watchstore.resources.products_solstice_diver
import dev.watchstore.resources.products_spring_bar_tool
import dev.watchstore.resources.products_steel_bracelet
import dev.watchstore.resources.products_terrane_gmt
import dev.watchstore.resources.products_velvet_cushions
import dev.watchstore.resources.products_vesper_slim
import dev.watchstore.resources.products_walnut_prestige
import org.jetbrains.compose.resources.DrawableResource

/**
 * Product id -> generated artwork.
 *
 * The mapping has to be written out because Compose Resources accessors are
 * compile-time symbols, not runtime lookups. The files themselves are produced
 * by `tools/gen-mock-images.ts`, which fails if its manifest drifts from
 * SeedCatalog.kt.
 *
 * Returning null is safe: `ProductImage` falls back to its drawn silhouette, so
 * a product can exist before its artwork does.
 */
private val images: Map<String, DrawableResource> = mapOf(
    "meridian-38" to Res.drawable.products_meridian_38,
    "solstice-diver" to Res.drawable.products_solstice_diver,
    "onyx-chrono" to Res.drawable.products_onyx_chrono,
    "aurum-field" to Res.drawable.products_aurum_field,
    "nocturne-moon" to Res.drawable.products_nocturne_moon,
    "terrane-gmt" to Res.drawable.products_terrane_gmt,
    "halcyon-34" to Res.drawable.products_halcyon_34,
    "basalt-auto" to Res.drawable.products_basalt_auto,
    "cascade-diver" to Res.drawable.products_cascade_diver,
    "vesper-slim" to Res.drawable.products_vesper_slim,
    "walnut-prestige" to Res.drawable.products_walnut_prestige,
    "midnight-blue" to Res.drawable.products_midnight_blue,
    "emerald-hardshell" to Res.drawable.products_emerald_hardshell,
    "sand-linen" to Res.drawable.products_sand_linen,
    "cognac-roll" to Res.drawable.products_cognac_roll,
    "oyster-roll" to Res.drawable.products_oyster_roll,
    "bordeaux-presentation" to Res.drawable.products_bordeaux_presentation,
    "basalt-compact" to Res.drawable.products_basalt_compact,
    "cascade-trunk" to Res.drawable.products_cascade_trunk,
    "obsidian-black" to Res.drawable.products_obsidian_black,
    "velvet-cushions" to Res.drawable.products_velvet_cushions,
    "spring-bar-tool" to Res.drawable.products_spring_bar_tool,
    "leather-strap" to Res.drawable.products_leather_strap,
    "steel-bracelet" to Res.drawable.products_steel_bracelet,
)

fun imageFor(id: String): DrawableResource? = images[id]