package dev.watchstore.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.watchstore.core.designsystem.Gold

/**
 * Frosted-glass surfaces.
 *
 * True backdrop blur is not available identically on every Compose Multiplatform
 * target (`Modifier.blur` is Android-only and API 31+), so "glass" here is built
 * from a translucent white wash plus a light hairline. On a dark ground that
 * reads as frosted glass and, unlike a real blur, looks the same on iOS.
 */
object Glass {
    /** Very light wash that lifts the surface off the background. */
    val wash = Color.White.copy(alpha = 0.07f)

    /** Slightly denser wash for controls sitting over photography. */
    val washStrong = Color(0xFF100E0B).copy(alpha = 0.42f)

    /** Hairline that defines the glass edge. */
    val edge = Color.White.copy(alpha = 0.14f)

    val edgeSoft = Color.White.copy(alpha = 0.09f)
}

/** Circular glass control — the search-adjacent and detail-screen buttons. */
@Composable
fun GlassCircleButton(
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    background: Color = Glass.wash,
    borderColor: Color = Glass.edge,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, borderColor, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/** Rounded-rectangle glass control, used for the outlined cart button. */
@Composable
fun GlassRoundedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    cornerRadius: Dp = 16.dp,
    background: Color = Color.Transparent,
    borderColor: Color = Glass.edge,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(background)
            .border(1.dp, borderColor, shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/**
 * Filter / sliders glyph.
 *
 * Material Icons has no filter icon in the core set (only in the Extended set,
 * which Compose Multiplatform froze at 1.7.3), so this is drawn to match the
 * reference: three rails with offset handles.
 */
@Composable
fun FilterGlyph(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val stroke = h * 0.085f
        // (rail position, knob position along the rail)
        listOf(0.26f to 0.62f, 0.5f to 0.34f, 0.74f to 0.7f).forEach { (rail, knob) ->
            val y = h * rail
            drawLine(
                color = color,
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
            drawCircle(
                color = color,
                radius = stroke * 1.45f,
                center = Offset(w * knob, y),
            )
        }
    }
}

/**
 * The detail screen's pagination dots, shown on a translucent pill so they stay
 * legible over any photograph.
 */
@Composable
fun DotsPill(
    count: Int,
    selected: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(percent = 50))
            .background(Color(0xFF0B0906).copy(alpha = 0.45f))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val active = index == selected
            Box(
                modifier = Modifier
                    .size(width = if (active) 18.dp else 6.dp, height = 6.dp)
                    .clip(CircleShape)
                    .background(if (active) Gold else Color.White.copy(alpha = 0.55f)),
            )
        }
    }
}