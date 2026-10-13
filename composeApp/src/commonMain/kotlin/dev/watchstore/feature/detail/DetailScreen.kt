package dev.watchstore.feature.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.watchstore.core.designsystem.PillShape
import dev.watchstore.core.designsystem.brandDisplay
import dev.watchstore.core.ui.DotsPill
import dev.watchstore.core.ui.FinishSwatch
import dev.watchstore.core.ui.Glass
import dev.watchstore.core.ui.GlassCircleButton
import dev.watchstore.core.ui.GlassRoundedButton
import dev.watchstore.core.ui.HighlightStrip
import dev.watchstore.core.ui.PriceText
import dev.watchstore.core.ui.ProductArtwork
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.color
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    product: Product,
    pairedProduct: Product?,
    isFavorite: Boolean,
    quantityInCart: Int,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShare: (String) -> Unit,
    onAddToCart: () -> Unit,
    onOpenPaired: (String) -> Unit,
    onAddPaired: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gallery = product.gallery
    val pageCount = maxOf(gallery.size, 1)
    val pagerState = rememberPagerState(pageCount = { pageCount })

    // When the gallery is one shot per finish, pager page == selected finish, so
    // swiping changes the colour. Single-finish products carry a second angle
    // instead, and their swatch stays put.
    val scope = rememberCoroutineScope()
    val swatchFollowsPage = gallery.size == product.finishes.size && product.finishes.size > 1
    val selectedFinish = if (swatchFollowsPage) {
        pagerState.currentPage.coerceIn(0, product.finishes.lastIndex)
    } else {
        0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // ---- Gallery ---------------------------------------------------------
        // Takes whatever height the sheet leaves, so the pagination dots always
        // land on visible artwork instead of hiding behind the sheet.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            if (gallery.isEmpty()) {
                // No artwork: fall back to the drawn silhouette.
                dev.watchstore.core.ui.ProductImage(
                    product = product,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    ProductArtwork(
                        artwork = gallery[page],
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlassCircleButton(
                    onClick = onBack,
                    contentDescription = "Back",
                    size = 44.dp,
                    background = Glass.washStrong,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Spacer(Modifier.weight(1f))
                GlassCircleButton(
                    onClick = onToggleFavorite,
                    contentDescription = if (isFavorite) "Remove from favourites" else "Save to favourites",
                    size = 44.dp,
                    background = Glass.washStrong,
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp),
                        tint = if (isFavorite) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
                Spacer(Modifier.width(10.dp))
                GlassCircleButton(
                    onClick = { onShare(product.name) },
                    contentDescription = "Share ${product.name}",
                    size = 44.dp,
                    background = Glass.washStrong,
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            if (pageCount > 1) {
                DotsPill(
                    count = pageCount,
                    selected = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp),
                )
            }
        }

        // ---- Bottom sheet ----------------------------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                .background(MaterialTheme.colorScheme.background)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
                )
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp, bottom = 16.dp)
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 25.sp),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(12.dp))
                PriceText(
                    cents = product.priceCents,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = brandDisplay(),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 23.sp,
                    ),
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.width(14.dp))
                product.finishes.forEachIndexed { index, finish ->
                    FinishSwatch(
                        color = finish.tone.color(),
                        name = finish.name,
                        selected = index == selectedFinish,
                        onClick = {
                            if (swatchFollowsPage) {
                                // Drive the pager; the swatch follows back.
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }
                        },
                    )
                    Spacer(Modifier.width(8.dp))
                }
            }

            Spacer(Modifier.height(18.dp))
            HighlightStrip(highlights = product.highlights)

            if (pairedProduct != null) {
                Spacer(Modifier.height(14.dp))
                val paired = pairedProduct
                CompleteTheSet(
                    paired = paired,
                    onAdd = onAddPaired,
                    onOpen = { onOpenPaired(paired.id) },
                )
            }

            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(PillShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClick = onAddToCart),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (quantityInCart > 0) {
                            "Add to Cart · $quantityInCart in bag"
                        } else {
                            "Add to Cart"
                        },
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                GlassRoundedButton(
                    onClick = onAddToCart,
                    size = 56.dp,
                    cornerRadius = 16.dp,
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Add to cart",
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun CompleteTheSet(
    paired: Product,
    onAdd: () -> Unit,
    onOpen: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        dev.watchstore.core.ui.ProductImage(
            product = paired,
            modifier = Modifier
                .size(width = 56.dp, height = 68.dp)
                .clip(RoundedCornerShape(8.dp)),
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Complete the set",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = paired.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        Row(
            modifier = Modifier
                .clip(PillShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onAdd)
                .padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Add",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .clip(PillShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onOpen)
                .padding(horizontal = 12.dp, vertical = 7.dp),
        ) {
            Text(
                text = "View",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}