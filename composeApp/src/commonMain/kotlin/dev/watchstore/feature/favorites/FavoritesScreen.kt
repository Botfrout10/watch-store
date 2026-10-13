package dev.watchstore.feature.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import dev.watchstore.core.ui.StoreTopBar
import dev.watchstore.data.model.Product

@Composable
fun FavoritesScreen(
    favorites: List<Product>,
    cartCount: Int,
    onProductClick: (String) -> Unit,
    onBrowse: () -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                    title = "Saved",
                    onOpenCart = onOpenCart,
                    cartCount = cartCount,
                )
            }
        }

        if (favorites.isEmpty()) {
            item {
                EmptyState(
                    title = "Nothing saved yet",
                    body = "Tap the heart on any product and it will wait here for you.",
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

        items(favorites, key = { it.id }) { product ->
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                ProductRow(product = product, onClick = { onProductClick(product.id) })
            }
        }
    }
}