package dev.watchstore.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centered title between two actions, mirroring the reference layout.
 * `leading` may be null on screens that push back instead (detail).
 */
@Composable
fun StoreTopBar(
    title: String,
    onOpenCart: () -> Unit,
    cartCount: Int,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) leading() else Spacer(Modifier.width(4.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
        )
        if (trailing != null) {
            trailing()
            Spacer(Modifier.width(10.dp))
        }
        BoxedCartIcon(cartCount = cartCount, onClick = onOpenCart)
    }
}

/** Cart icon with an overlaid count badge — the reference's signature detail. */
@Composable
fun BoxedCartIcon(
    cartCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.layout.Box(modifier = modifier) {
        GhostIconButton(
            icon = Icons.Default.ShoppingCart,
            contentDescription = "Open cart",
            onClick = onClick,
        )
        if (cartCount > 0) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp),
            ) {
                CountBadge(count = cartCount)
            }
        }
    }
}

/** Standard top padding that clears the status bar while drawing behind it. */
@Composable
fun statusBarPadding(): Int = WindowInsets.statusBars.asPaddingValues().calculateTopPadding().value.toInt()