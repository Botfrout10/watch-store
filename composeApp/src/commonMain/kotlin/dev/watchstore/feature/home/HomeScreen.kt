package dev.watchstore.feature.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import dev.watchstore.core.designsystem.BaseDark
import dev.watchstore.core.designsystem.Gold
import dev.watchstore.core.designsystem.GoldSoft
import dev.watchstore.core.designsystem.PillShape
import dev.watchstore.core.ui.PillSearch
import dev.watchstore.core.ui.PriceText
import dev.watchstore.core.ui.ProductCard
import dev.watchstore.core.ui.ProductImageCard
import dev.watchstore.core.ui.SectionHeader
import dev.watchstore.core.ui.StoreTopBar
import dev.watchstore.data.model.Hero
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.ProductKind

@Composable
fun HomeScreen(
    heroes: List<Hero>,
    popular: List<Product>,
    featured: List<Product>,
    favorites: Set<String>,
    cartCount: Int,
    onProductClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onBrowseKind: (ProductKind) -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 28.dp),
    ) {
        item {
            Column(
                modifier = Modifier
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                StoreTopBar(
                    title = "Atelier",
                    onOpenCart = onOpenCart,
                    cartCount = cartCount,
                )
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            PillSearch(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search watches, cases, straps",
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        item {
            Spacer(Modifier.height(22.dp))
            HeroCarousel(heroes = heroes, onBrowseKind = onBrowseKind)
        }

        item {
            Spacer(Modifier.height(30.dp))
            SectionHeader(
                title = "Popular now",
                actionLabel = "See all",
                onAction = { onBrowseKind(ProductKind.WATCH) },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(14.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(popular, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        isFavorite = product.id in favorites,
                        onToggleFavorite = { onToggleFavorite(product.id) },
                        modifier = Modifier.width(172.dp),
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            SectionHeader(
                title = "Featured",
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(14.dp))
            featured.forEach { product ->
                FeaturedRow(
                    product = product,
                    isFavorite = product.id in favorites,
                    onClick = { onProductClick(product.id) },
                    onToggleFavorite = { onToggleFavorite(product.id) },
                )
            }
        }
    }
}

@Composable
private fun HeroCarousel(
    heroes: List<Hero>,
    onBrowseKind: (ProductKind) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { heroes.size })

    Box {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp,
        ) { page ->
            val hero = heroes[page]
            HeroCard(hero = hero, onCta = { onBrowseKind(hero.targetKind) })
        }

        // Pagination dots sit bottom-end rather than bottom-centre: the CTA
        // already owns the bottom-start of the card and they were colliding.
        if (heroes.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                repeat(heroes.size) { index ->
                    val active = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(5.dp)
                            .width(if (active) 20.dp else 5.dp)
                            .clip(CircleShape)
                            .background(if (active) Gold else MaterialTheme.colorScheme.outline),
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroCard(
    hero: Hero,
    onCta: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(322.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                Brush.radialGradient(
                    0f to lerp(BaseDark, Gold, 0.24f),
                    0.55f to lerp(BaseDark, Gold, 0.09f),
                    1f to BaseDark,
                    center = Offset(900f, 120f),
                    radius = 1500f,
                ),
            )
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.extraLarge)
            .padding(24.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = hero.eyebrow.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Gold,
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = hero.title,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = hero.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(0.82f),
            )
        }

        dev.watchstore.core.ui.GoldButton(
            text = hero.cta,
            onClick = onCta,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .width(196.dp),
        )
    }
}

@Composable
private fun FeaturedRow(
    product: Product,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 7.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProductImageCard(
            product = product,
            modifier = Modifier.size(width = 84.dp, height = 104.dp),
            shape = MaterialTheme.shapes.small,
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.kind.label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Gold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
            )
            Spacer(Modifier.height(6.dp))
            PriceText(cents = product.priceCents)
        }
    }
}