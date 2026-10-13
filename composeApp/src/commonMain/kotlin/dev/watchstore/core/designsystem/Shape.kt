package dev.watchstore.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Generous radii are the cheapest way to read as "premium" — soft corners echo
 * the rounded case profiles of the products themselves.
 */
val WatchStoreShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

/** Card corner used by product tiles; large enough to feel soft, small enough to tile. */
val ProductCardShape = RoundedCornerShape(22.dp)

/** Pill corner for search fields, chips and CTA-adjacent surfaces. */
val PillShape = RoundedCornerShape(percent = 50)