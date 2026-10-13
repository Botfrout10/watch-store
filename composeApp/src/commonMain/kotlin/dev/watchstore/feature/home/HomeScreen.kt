package dev.watchstore.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.watchstore.core.designsystem.BaseDark
import dev.watchstore.core.designsystem.Gold
import dev.watchstore.core.designsystem.PillShape
import dev.watchstore.core.designsystem.PlayfairFamily
import dev.watchstore.core.ui.CatalogCard
import dev.watchstore.core.ui.DotsPill
import dev.watchstore.core.ui.FilterGlyph
import dev.watchstore.core.ui.Glass
import dev.watchstore.core.ui.GlassCircleButton
import dev.watchstore.core.ui.PillSearch
import dev.watchstore.core.ui.ProductArtwork
import dev.watchstore.core.ui.SectionHeader
import dev.watchstore.data.model.Hero
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.ProductKind

@Composable
fun HomeScreen(
    heroes: List<Hero>,
    heroProducts: Map<String, Product>,
    popular: List<Product>,
    favorites: Set<String>,
    cartCount: Int,
    onProductClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onOpenCart: () -> Unit,
    onOpenKind: (ProductKind) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filtersOpen by rememberSaveable { mutableStateOf(false) }

    val searched = remember(query, popular) {
        if (query.isBlank()) {
            popular
        } else {
            popular.filter {
                query.trim().lowercase() in it.name.lowercase() ||
                    query.trim().lowercase() in it.brand.lowercase()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Column(
                modifier = Modifier
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp),
            ) {
                HomeTopBar(
                    cartCount = cartCount,
                    onOpenCart = onOpenCart,
                    onMenu = { filtersOpen = !filtersOpen },
                )

                Spacer(Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PillSearch(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = "Search watches, cases…",
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(10.dp))
                    GlassCircleButton(
                        onClick = { filtersOpen = !filtersOpen },
                        contentDescription = if (filtersOpen) {
                            "Hide category filters"
                        } else {
                            "Show category filters"
                        },
                        size = 44.dp,
                    ) {
                        FilterGlyph(
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }

                // Collapsible category filter, driven by the button beside search.
                AnimatedVisibility(
                    visible = filtersOpen,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    CategoryFilterRow(
                        onSelect = { kind ->
                            filtersOpen = false
                            onOpenKind(kind)
                        },
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
            HeroCarousel(heroes = heroes, heroProducts = heroProducts, onOpenKind = onOpenKind)
        }

        item {
            Spacer(Modifier.height(24.dp))
            SectionHeader(
                title = "Popular",
                actionLabel = "View all",
                onAction = { onOpenKind(ProductKind.WATCH) },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(Modifier.height(14.dp))

            if (searched.isEmpty()) {
                Text(
                    text = "Nothing matches \"${query.trim()}\".",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            } else {
                // Fixed two-column grid, chunked into rows: the catalogue is
                // short enough that a nested lazy grid would be overkill.
                searched.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(13.dp),
                    ) {
                        row.forEach { product ->
                            CatalogCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onAddToCart = { onAddToCart(product.id) },
                                isFavorite = product.id in favorites,
                                onToggleFavorite = { onToggleFavorite(product.id) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = 14.dp),
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    cartCount: Int,
    onOpenCart: () -> Unit,
    onMenu: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlassCircleButton(
            onClick = onMenu,
            contentDescription = "Browse categories",
            size = 40.dp,
            background = Color.Transparent,
            borderColor = Color.Transparent,
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = "Atelier",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = PlayfairFamily,
                fontSize = 17.sp,
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        Box(contentAlignment = Alignment.TopEnd) {
            GlassCircleButton(
                onClick = onOpenCart,
                contentDescription = "Open bag",
                size = 40.dp,
                background = Color.Transparent,
                borderColor = Color.Transparent,
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            // Kept small and offset outward so it never covers the cart glyph.
            CartBadge(cartCount)
        }
    }
}

@Composable
private fun CartBadge(count: Int) {
    if (count <= 0) return
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Gold)
            .border(1.dp, MaterialTheme.colorScheme.background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (count > 9) "9+" else count.toString(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun CategoryFilterRow(
    onSelect: (ProductKind) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(ProductKind.entries.toList()) { kind ->
            Text(
                text = kind.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clip(PillShape)
                    .background(Glass.wash)
                    .border(1.dp, Glass.edge, PillShape)
                    .clickable { onSelect(kind) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
            )
        }
    }
}

@Composable
private fun HeroCarousel(
    heroes: List<Hero>,
    heroProducts: Map<String, Product>,
    onOpenKind: (ProductKind) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { heroes.size })

    Box {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp,
        ) { page ->
            val hero = heroes[page]
            HeroCard(
                hero = hero,
                product = heroProducts[hero.productId],
                onCta = { onOpenKind(hero.targetKind) },
            )
        }

        if (heroes.size > 1) {
            DotsPill(
                count = heroes.size,
                selected = pagerState.currentPage,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
            )
        }
    }
}

/**
 * Hero panel: artwork bleeds off the right edge, copy sits left. Matches the
 * reference proportions — 312dp tall, 24dp corners, 34dp CTA.
 */
@Composable
private fun HeroCard(
    hero: Hero,
    product: Product?,
    onCta: () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    val artwork = product?.gallery?.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(312.dp)
            .clip(shape)
            .background(BaseDark)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape),
    ) {
        if (artwork != null) {
            ProductArtwork(
                artwork = artwork,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .fillMaxWidth(0.58f),
            )
            // Fade the artwork into the copy side so the text always wins.
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .fillMaxWidth(0.58f)
                    .background(
                        Brush.horizontalGradient(
                            0f to BaseDark,
                            1f to Color.Transparent,
                        ),
                    ),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(0.62f)
                .padding(start = 20.dp, top = 22.dp, bottom = 22.dp),
        ) {
            Text(
                text = hero.eyebrow.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = Gold,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = hero.title,
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 27.sp),
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = hero.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.weight(1f))
            // Sits clear of the pagination dots pinned to the card's bottom edge.
            HeroCta(
                text = hero.cta,
                onClick = onCta,
                modifier = Modifier.padding(bottom = 34.dp),
            )
        }
    }
}

@Composable
private fun HeroCta(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(34.dp)
            .clip(PillShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}