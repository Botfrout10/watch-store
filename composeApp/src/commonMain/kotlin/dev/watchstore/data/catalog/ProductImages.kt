package dev.watchstore.data.catalog

import dev.watchstore.resources.Res
import dev.watchstore.resources.products_aurum_field_f0
import dev.watchstore.resources.products_aurum_field_f1
import dev.watchstore.resources.products_basalt_auto_f0
import dev.watchstore.resources.products_basalt_auto_f1
import dev.watchstore.resources.products_basalt_compact_f0
import dev.watchstore.resources.products_basalt_compact_f0_alt
import dev.watchstore.resources.products_bordeaux_presentation_f0
import dev.watchstore.resources.products_bordeaux_presentation_f0_alt
import dev.watchstore.resources.products_cascade_diver_f0
import dev.watchstore.resources.products_cascade_diver_f1
import dev.watchstore.resources.products_cascade_trunk_f0
import dev.watchstore.resources.products_cascade_trunk_f1
import dev.watchstore.resources.products_cognac_roll_f0
import dev.watchstore.resources.products_cognac_roll_f0_alt
import dev.watchstore.resources.products_emerald_hardshell_f0
import dev.watchstore.resources.products_emerald_hardshell_f0_alt
import dev.watchstore.resources.products_halcyon_34_f0
import dev.watchstore.resources.products_halcyon_34_f1
import dev.watchstore.resources.products_leather_strap_f0
import dev.watchstore.resources.products_leather_strap_f0_alt
import dev.watchstore.resources.products_meridian_38_f0
import dev.watchstore.resources.products_meridian_38_f1
import dev.watchstore.resources.products_midnight_blue_f0
import dev.watchstore.resources.products_midnight_blue_f0_alt
import dev.watchstore.resources.products_nocturne_moon_f0
import dev.watchstore.resources.products_nocturne_moon_f1
import dev.watchstore.resources.products_obsidian_black_f0
import dev.watchstore.resources.products_obsidian_black_f0_alt
import dev.watchstore.resources.products_onyx_chrono_f0
import dev.watchstore.resources.products_onyx_chrono_f0_alt
import dev.watchstore.resources.products_oyster_roll_f0
import dev.watchstore.resources.products_oyster_roll_f0_alt
import dev.watchstore.resources.products_sand_linen_f0
import dev.watchstore.resources.products_sand_linen_f0_alt
import dev.watchstore.resources.products_solstice_diver_f0
import dev.watchstore.resources.products_solstice_diver_f1
import dev.watchstore.resources.products_spring_bar_tool_f0
import dev.watchstore.resources.products_spring_bar_tool_f0_alt
import dev.watchstore.resources.products_steel_bracelet_f0
import dev.watchstore.resources.products_steel_bracelet_f0_alt
import dev.watchstore.resources.products_terrane_gmt_f0
import dev.watchstore.resources.products_terrane_gmt_f1
import dev.watchstore.resources.products_velvet_cushions_f0
import dev.watchstore.resources.products_velvet_cushions_f0_alt
import dev.watchstore.resources.products_vesper_slim_f0
import dev.watchstore.resources.products_vesper_slim_f0_alt
import dev.watchstore.resources.products_walnut_prestige_f0
import dev.watchstore.resources.products_walnut_prestige_f0_alt
import org.jetbrains.compose.resources.DrawableResource

/**
 * Product id -> artwork gallery.
 *
 * GENERATED FILE — do not edit by hand. Run `bun run tools/gen-mock-images.ts`.
 *
 * One shot per finish, so choosing a colour swatch changes the photograph.
 * Single-finish products carry a second, closer angle so the detail pager is
 * always swipeable.
 *
 * Returning an empty list is safe: ProductImage falls back to its drawn
 * silhouette, so a product can exist before its artwork does.
 */
private val galleries: Map<String, List<DrawableResource>> = mapOf(
    "meridian-38" to listOf(
        Res.drawable.products_meridian_38_f0,
        Res.drawable.products_meridian_38_f1,
    ),
    "solstice-diver" to listOf(
        Res.drawable.products_solstice_diver_f0,
        Res.drawable.products_solstice_diver_f1,
    ),
    "onyx-chrono" to listOf(
        Res.drawable.products_onyx_chrono_f0,
        Res.drawable.products_onyx_chrono_f0_alt,
    ),
    "aurum-field" to listOf(
        Res.drawable.products_aurum_field_f0,
        Res.drawable.products_aurum_field_f1,
    ),
    "nocturne-moon" to listOf(
        Res.drawable.products_nocturne_moon_f0,
        Res.drawable.products_nocturne_moon_f1,
    ),
    "terrane-gmt" to listOf(
        Res.drawable.products_terrane_gmt_f0,
        Res.drawable.products_terrane_gmt_f1,
    ),
    "halcyon-34" to listOf(
        Res.drawable.products_halcyon_34_f0,
        Res.drawable.products_halcyon_34_f1,
    ),
    "basalt-auto" to listOf(
        Res.drawable.products_basalt_auto_f0,
        Res.drawable.products_basalt_auto_f1,
    ),
    "cascade-diver" to listOf(
        Res.drawable.products_cascade_diver_f0,
        Res.drawable.products_cascade_diver_f1,
    ),
    "vesper-slim" to listOf(
        Res.drawable.products_vesper_slim_f0,
        Res.drawable.products_vesper_slim_f0_alt,
    ),
    "walnut-prestige" to listOf(
        Res.drawable.products_walnut_prestige_f0,
        Res.drawable.products_walnut_prestige_f0_alt,
    ),
    "midnight-blue" to listOf(
        Res.drawable.products_midnight_blue_f0,
        Res.drawable.products_midnight_blue_f0_alt,
    ),
    "emerald-hardshell" to listOf(
        Res.drawable.products_emerald_hardshell_f0,
        Res.drawable.products_emerald_hardshell_f0_alt,
    ),
    "sand-linen" to listOf(
        Res.drawable.products_sand_linen_f0,
        Res.drawable.products_sand_linen_f0_alt,
    ),
    "cognac-roll" to listOf(
        Res.drawable.products_cognac_roll_f0,
        Res.drawable.products_cognac_roll_f0_alt,
    ),
    "oyster-roll" to listOf(
        Res.drawable.products_oyster_roll_f0,
        Res.drawable.products_oyster_roll_f0_alt,
    ),
    "bordeaux-presentation" to listOf(
        Res.drawable.products_bordeaux_presentation_f0,
        Res.drawable.products_bordeaux_presentation_f0_alt,
    ),
    "basalt-compact" to listOf(
        Res.drawable.products_basalt_compact_f0,
        Res.drawable.products_basalt_compact_f0_alt,
    ),
    "cascade-trunk" to listOf(
        Res.drawable.products_cascade_trunk_f0,
        Res.drawable.products_cascade_trunk_f1,
    ),
    "obsidian-black" to listOf(
        Res.drawable.products_obsidian_black_f0,
        Res.drawable.products_obsidian_black_f0_alt,
    ),
    "velvet-cushions" to listOf(
        Res.drawable.products_velvet_cushions_f0,
        Res.drawable.products_velvet_cushions_f0_alt,
    ),
    "spring-bar-tool" to listOf(
        Res.drawable.products_spring_bar_tool_f0,
        Res.drawable.products_spring_bar_tool_f0_alt,
    ),
    "leather-strap" to listOf(
        Res.drawable.products_leather_strap_f0,
        Res.drawable.products_leather_strap_f0_alt,
    ),
    "steel-bracelet" to listOf(
        Res.drawable.products_steel_bracelet_f0,
        Res.drawable.products_steel_bracelet_f0_alt,
    ),
)

fun galleryFor(id: String): List<DrawableResource> = galleries[id].orEmpty()
