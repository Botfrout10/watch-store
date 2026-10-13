package dev.watchstore.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.watchstore.data.model.Highlight

/**
 * Three-up benefit strip: icon, title, micro-caption.
 *
 * The caption is deliberately tiny and low-contrast — it supports the title, it
 * doesn't compete with it.
 */
@Composable
fun HighlightStrip(
    highlights: List<Highlight>,
    modifier: Modifier = Modifier,
) {
    if (highlights.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        highlights.take(3).forEachIndexed { index, highlight ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.outline),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HighlightGlyph(
                    icon = highlight.icon,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.height(9.dp))
                Text(
                    text = highlight.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = highlight.caption,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
        }
    }
}

/** Small label + value pill, used for order summary rows. */
@Composable
fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasise: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (emphasise) {
                MaterialTheme.colorScheme.onBackground
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
        // Style the already-formatted string rather than re-parsing it back to
        // cents — "$1,240.00".toLongOrNull() is null, which silently showed $0.00.
        if (emphasise) {
            Text(
                text = value,
                style = dev.watchstore.core.designsystem.PriceStyle,
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/** Rounded surface used as a card container across the app. */
@Composable
fun PanelSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer),
    ) {
        content()
    }
}