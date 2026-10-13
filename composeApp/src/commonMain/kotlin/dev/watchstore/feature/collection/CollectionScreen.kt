package dev.watchstore.feature.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.watchstore.core.ui.CatalogCard
import dev.watchstore.core.ui.EmptyState
import dev.watchstore.core.ui.PillSearch
import dev.watchstore.core.ui.SectionHeader
import dev.watchstore.core.ui.SegmentedTabs
import dev.watchstore.core.ui.StoreTopBar
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.ProductKind

@Composable
fun CollectionScreen(
    products: List<Product>,
    favorites: Set<String>,
    cartCount: Int,
    onProductClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedKind by rememberSaveable { mutableStateOf(ProductKind.WATCH) }
    var query by rememberSaveable { mutableStateOf("") }

    val kinds = listOf(ProductKind.WATCH, ProductKind.BOX, ProductKind.ACCESSORY)

    val visible = products
        .filter { it.kind == selectedKind }
        .filter {
            val q = query.trim()
            q.isEmpty() ||
                q in it.name.lowercase() ||
                q in it.brand.lowercase() ||
                q in it.tagline.lowercase()
        }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .padding(vertical = 12.dp),
            ) {
                StoreTopBar(
                    title = "Collection",
                    onOpenCart = onOpenCart,
                    cartCount = cartCount,
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                PillSearch(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "Search ${selectedKind.label.lowercase()}",
                )
                Spacer(Modifier.height(14.dp))
                SegmentedTabs(
                    options = kinds.map { it.label },
                    selectedIndex = kinds.indexOf(selectedKind),
                    onSelect = { selectedKind = kinds[it] },
                )
                Spacer(Modifier.height(22.dp))
                SectionHeader(
                    title = "${selectedKind.label} · ${visible.size}",
                    modifier = Modifier.padding(bottom = 14.dp),
                )
            }
        }

        if (visible.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(
                    title = "Nothing here yet",
                    body = "No ${selectedKind.label.lowercase()} match \"${query.trim()}\". " +
                        "Try a different search or browse another category.",
                )
            }
        } else {
            items(visible, key = { it.id }) { product ->
                CatalogCard(
                    product = product,
                    onClick = { onProductClick(product.id) },
                    onAddToCart = { onAddToCart(product.id) },
                    isFavorite = product.id in favorites,
                    onToggleFavorite = { onToggleFavorite(product.id) },
                )
            }
        }
    }
}