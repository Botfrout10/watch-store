package dev.watchstore.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.watchstore.data.model.HighlightIcon

/**
 * Hand-drawn line-art glyphs for the three-up highlight strip.
 *
 * Material Icons Extended is frozen at 1.7.3 and its generic shapes would pull
 * the eye away from the brand. Drawing them keeps the weight consistent with the
 * rest of the app and costs no dependency.
 */
@Composable
fun HighlightGlyph(
    icon: HighlightIcon,
    tint: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 1.7f,
) {
    Canvas(modifier) {
        drawGlyph(icon, tint, strokeWidth)
    }
}

private fun DrawScope.drawGlyph(icon: HighlightIcon, tint: Color, strokeWidth: Float) {
    val s = minOf(size.width, size.height) / 24f
    fun x(v: Float) = v * s
    fun y(v: Float) = v * s
    fun o(xx: Float, yy: Float) = Offset(x(xx), y(yy))

    val w = strokeWidth * s
    val style = Stroke(width = w, cap = StrokeCap.Round)

    when (icon) {
        HighlightIcon.Wood -> {
            // Three planks with a knot.
            drawLine(tint, o(7f, 4f), o(7f, 20f), w, StrokeCap.Round)
            drawLine(tint, o(12f, 4f), o(12f, 20f), w, StrokeCap.Round)
            drawLine(tint, o(17f, 4f), o(17f, 20f), w, StrokeCap.Round)
            drawCircle(tint, radius = 2.4f * s, center = o(12f, 13f), style = Stroke(width = w))
        }

        HighlightIcon.Interior -> {
            // Liner: outer shell with a cushion bulge.
            drawRoundRect(
                color = tint,
                topLeft = o(4f, 6f),
                size = Size(x(16f), y(12f)),
                cornerRadius = CornerRadius(2.5f * s),
                style = Stroke(width = w),
            )
            drawArc(
                color = tint,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = o(7.5f, 7.5f),
                size = Size(x(9f), y(9f)),
                style = Stroke(width = w, cap = StrokeCap.Round),
            )
        }

        HighlightIcon.Lock -> {
            drawArc(
                color = tint,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = o(7f, 4f),
                size = Size(x(10f), y(10f)),
                style = Stroke(width = w, cap = StrokeCap.Round),
            )
            drawRoundRect(
                color = tint,
                topLeft = o(5.5f, 10f),
                size = Size(x(13f), y(9f)),
                cornerRadius = CornerRadius(2.2f * s),
                style = Stroke(width = w),
            )
        }

        HighlightIcon.Sapphire -> {
            // Brilliant-cut gem: crown plus facets.
            val gem = Path().apply {
                moveTo(x(12f), y(3.5f))
                lineTo(x(20f), y(9.5f))
                lineTo(x(12f), y(20.5f))
                lineTo(x(4f), y(9.5f))
                close()
            }
            drawPath(gem, tint, style = Stroke(width = w, join = StrokeJoin.Round))
            drawLine(tint, o(4f, 9.5f), o(20f, 9.5f), w, StrokeCap.Round)
            drawLine(tint, o(12f, 3.5f), o(8.5f, 9.5f), w, StrokeCap.Round)
            drawLine(tint, o(12f, 3.5f), o(15.5f, 9.5f), w, StrokeCap.Round)
        }

        HighlightIcon.Movement -> {
            drawCircle(tint, radius = 6f * s, center = o(12f, 12f), style = Stroke(width = w))
            drawCircle(tint, radius = 2.4f * s, center = o(12f, 12f), style = Stroke(width = w))
            for (i in 0 until 6) {
                val a = Math.toRadians((i * 60 - 90).toDouble())
                val c = kotlin.math.cos(a).toFloat()
                val sn = kotlin.math.sin(a).toFloat()
                drawLine(
                    tint,
                    Offset(12f * s + 6f * s * c, 12f * s + 6f * s * sn),
                    Offset(12f * s + 8.4f * s * c, 12f * s + 8.4f * s * sn),
                    w,
                    StrokeCap.Round,
                )
            }
        }

        HighlightIcon.Water -> {
            val drop = Path().apply {
                moveTo(x(12f), y(3f))
                cubicTo(x(17.5f), y(10f), x(18.5f), y(13f), x(18.5f), y(14.5f))
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(o(5.5f, 9f), o(18.5f, 21f)),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false,
                )
                cubicTo(x(5.5f), y(13f), x(6.5f), y(10f), x(12f), y(3f))
                close()
            }
            drawPath(drop, tint, style = Stroke(width = w, join = StrokeJoin.Round))
        }

        HighlightIcon.Craft -> {
            // Caliper: two jaws meeting at a pivot.
            drawLine(tint, o(5f, 19f), o(13f, 7f), w, StrokeCap.Round)
            drawLine(tint, o(13f, 7f), o(19f, 5f), w, StrokeCap.Round)
            drawLine(tint, o(13f, 7f), o(11f, 12f), w, StrokeCap.Round)
            drawCircle(tint, radius = 1.9f * s, center = o(13f, 7f), style = Stroke(width = w))
        }

        HighlightIcon.Warranty -> {
            val shield = Path().apply {
                moveTo(x(12f), y(3f))
                lineTo(x(19.5f), y(6.2f))
                lineTo(x(19.5f), y(12f))
                cubicTo(x(19.5f), y(16.5f), x(16f), y(19.6f), x(12f), y(21f))
                cubicTo(x(8f), y(19.6f), x(4.5f), y(16.5f), x(4.5f), y(12f))
                lineTo(x(4.5f), y(6.2f))
                close()
            }
            drawPath(shield, tint, style = Stroke(width = w, join = StrokeJoin.Round))
            drawLine(tint, o(9f, 12.4f), o(11.4f, 15f), w, StrokeCap.Round)
            drawLine(tint, o(11.4f, 15f), o(15.4f, 9.8f), w, StrokeCap.Round)
        }
    }
}