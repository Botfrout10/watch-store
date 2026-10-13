package dev.watchstore.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.watchstore.core.designsystem.WatchStoreTheme
import dev.watchstore.data.model.ProductKind
import dev.watchstore.feature.cart.CartScreen
import dev.watchstore.feature.collection.CollectionScreen
import dev.watchstore.feature.detail.DetailScreen
import dev.watchstore.feature.favorites.FavoritesScreen
import dev.watchstore.feature.home.HomeScreen
import dev.watchstore.feature.profile.ProfileScreen
import dev.watchstore.feature.store.StoreViewModel
import dev.watchstore.platform.shareContent

/**
 * Shown while the catalogue is in flight.
 *
 * The seed guarantees `Ready` always arrives, so this is brief — a wrong-looking
 * failure state here would be worse than a short pause.
 */
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp),
        )
    }
}

/**
 * Destinations.
 *
 * Navigation is a plain back stack rather than Nav3 for now: the `navigation3-ui`
 * artifact resolves to an empty stub on this toolchain, so there is nothing to
 * import. The behaviour needed here — tab roots, push/pop detail, system back —
 * is fully covered by this, and swapping Nav3 in later is confined to this file.
 */
sealed interface Destination {
    data object Home : Destination
    data object Collection : Destination
    data object Favorites : Destination
    data object Bag : Destination
    data object Profile : Destination
    data class Detail(val productId: String) : Destination
}

private fun Destination.asTab(): AppTab? = when (this) {
    Destination.Home -> AppTab.Home
    Destination.Collection -> AppTab.Collection
    Destination.Favorites -> AppTab.Favorites
    Destination.Profile -> AppTab.Profile
    // The bag is reachable from the top bar, not the bottom bar.
    Destination.Bag, is Destination.Detail -> null
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WatchStoreApp() {
    WatchStoreTheme {
        val store: StoreViewModel = viewModel { StoreViewModel() }

        val favorites by store.favorites.collectAsState()
        val cart by store.cart.collectAsState()
        val catalog by store.catalog.collectAsState()

        val backStack = remember { mutableStateListOf<Destination>(Destination.Home) }
        val current = backStack.last()
        val cartCount = cart.values.sum()

        // Recomputed from the loaded snapshot rather than cached in the view
        // model, so it can never disagree with the catalogue actually shown.
        val heroProducts = remember(catalog, store.heroes) {
            store.heroes.mapNotNull { hero -> store.product(hero.productId)?.let { hero.productId to it } }.toMap()
        }

        fun popBack() {
            if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
        }

        BackHandler(enabled = backStack.size > 1) { popBack() }

        val openDetail: (String) -> Unit = { backStack.add(Destination.Detail(it)) }

        val selectTab: (AppTab) -> Unit = { tab ->
            backStack.clear()
            backStack.add(
                when (tab) {
                    AppTab.Home -> Destination.Home
                    AppTab.Collection -> Destination.Collection
                    AppTab.Favorites -> Destination.Favorites
                    AppTab.Profile -> Destination.Profile
                },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (!catalog.isReady) {
                    LoadingState()
                } else {
                when (val destination = current) {
                    Destination.Home -> HomeScreen(
                        heroes = store.heroes,
                        heroProducts = heroProducts,
                        popular = store.popular,
                        favorites = favorites,
                        cartCount = cartCount,
                        onProductClick = openDetail,
                        onToggleFavorite = store::toggleFavorite,
                        onAddToCart = store::addToCart,
                        onOpenCart = { backStack.add(Destination.Bag) },
                        onOpenKind = { selectTab(AppTab.Collection) },
                    )

                    Destination.Collection -> CollectionScreen(
                        products = store.products,
                        favorites = favorites,
                        cartCount = cartCount,
                        onProductClick = openDetail,
                        onToggleFavorite = store::toggleFavorite,
                        onAddToCart = store::addToCart,
                        onOpenCart = { backStack.add(Destination.Bag) },
                    )

                    Destination.Favorites -> FavoritesScreen(
                        favorites = store.favoriteProducts(),
                        cartCount = cartCount,
                        onProductClick = openDetail,
                        onBrowse = { selectTab(AppTab.Collection) },
                        onOpenCart = { backStack.add(Destination.Bag) },
                    )

                    Destination.Bag -> CartScreen(
                        lines = store.cartLines(),
                        cartCount = cartCount,
                        onProductClick = openDetail,
                        onSetQuantity = store::setQuantity,
                        onBrowse = { selectTab(AppTab.Collection) },
                        onBack = ::popBack,
                    )

                    Destination.Profile -> ProfileScreen(
                        cartCount = cartCount,
                        catalogSource = catalog.source,
                        onOpenCart = { backStack.add(Destination.Bag) },
                    )

                    is Destination.Detail -> {
                        val product = store.product(destination.productId)
                        if (product == null) {
                            selectTab(AppTab.Home)
                        } else {
                            DetailScreen(
                                product = product,
                                pairedProduct = product.pairsWithId?.let(store::product),
                                isFavorite = product.id in favorites,
                                quantityInCart = cart[product.id] ?: 0,
                                onBack = ::popBack,
                                onToggleFavorite = { store.toggleFavorite(product.id) },
                                onShare = { name ->
                                    shareContent(name, "${product.brand} ${product.name} — Atelier")
                                },
                                onAddToCart = { store.addToCart(product.id) },
                                onOpenPaired = openDetail,
                                onAddPaired = { product.pairsWithId?.let(store::addToCart) },
                            )
                        }
                    }
                }
                }
            }

            current.asTab()?.let { tab ->
                AppBottomBar(current = tab, onSelect = selectTab)
            }
        }
    }
}