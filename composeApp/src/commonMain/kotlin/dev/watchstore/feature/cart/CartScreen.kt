package dev.watchstore.feature.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.watchstore.core.ui.EmptyState
import dev.watchstore.core.ui.GoldButton
import dev.watchstore.core.ui.ProductRow
import dev.watchstore.core.ui.QuantityStepper
import dev.watchstore.core.ui.StoreTopBar
import dev.watchstore.core.ui.SummaryRow
import dev.watchstore.data.model.CartLine
import dev.watchstore.data.model.formatPrice

private const val SHIPPING_FLAT_CENTS = 1_200L

@Composable
fun CartScreen(
    lines: List<CartLine>,
    cartCount: Int,
    onProductClick: (String) -> Unit,
    onSetQuantity: (String, Int) -> Unit,
    onBrowse: () -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val subtotal = lines.sumOf { it.product.priceCents * it.quantity }
    val shipping = if (subtotal == 0L) 0L else SHIPPING_FLAT_CENTS
    val total = subtotal + shipping

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 28.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                StoreTopBar(
                    title = "Bag",
                    onOpenCart = onOpenCart,
                    cartCount = cartCount,
                )
            }
        }

        if (lines.isEmpty()) {
            item {
                EmptyState(
                    title = "Your bag is empty",
                    body = "Nothing in here yet. Start with the collection — most people buy a case first, then the watch.",
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                ) {
                    GoldButton(
                        text = "Browse the collection",
                        onClick = onBrowse,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            return@LazyColumn
        }

        items(lines, key = { it.product.id }) { line ->
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                ProductRow(
                    product = line.product,
                    onClick = { onProductClick(line.product.id) },
                    trailing = {
                        QuantityStepper(
                            quantity = line.quantity,
                            onChange = { onSetQuantity(line.product.id, it) },
                        )
                    },
                )
                Spacer(Modifier.height(1.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline),
                )
            }
        }

        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .navigationBarsPadding(),
            ) {
                Spacer(Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.shapes.medium,
                        )
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SummaryRow(label = "Subtotal", value = formatPrice(subtotal))
                    SummaryRow(label = "Shipping", value = formatPrice(shipping))
                    Spacer(Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline),
                    )
                    Spacer(Modifier.height(2.dp))
                    SummaryRow(label = "Total", value = formatPrice(total), emphasise = true)
                }
                Spacer(Modifier.height(18.dp))
                GoldButton(
                    text = "Checkout",
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Checkout is not wired up in this build.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}