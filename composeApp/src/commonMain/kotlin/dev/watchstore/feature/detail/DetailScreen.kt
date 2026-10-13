package dev.watchstore.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.watchstore.core.designsystem.PillShape
import dev.watchstore.core.designsystem.PlayfairFamily
import dev.watchstore.core.ui.FinishSwatch
import dev.watchstore.core.ui.GhostIconButton
import dev.watchstore.core.ui.GoldButton
import dev.watchstore.core.ui.GoldIconButton
import dev.watchstore.core.ui.HighlightStrip
import dev.watchstore.core.ui.PriceText
import dev.watchstore.core.ui.ProductImage
import dev.watchstore.core.ui.ProductImageHero
import dev.watchstore.core.ui.StarRow
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.color

@Composable
fun DetailScreen(
    product: Product,
    pairedProduct: Product?,
    isFavorite: Boolean,
    quantityInCart: Int,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit,
    onOpenPaired: (String) -> Unit,
    onAddPaired: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedFinish by rememberSaveable(product.id) { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 28.dp),
    ) {
        // ---- Hero image with floating controls ------------------------------
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                ProductImageHero(
                    product = product,
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.ui.graphics.RectangleShape,
                )

                // Controls are declared *after* the image so they paint on top:
                // children of a Box draw in declaration order.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        GhostIconButton(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            onClick = onBack,
                            background = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                        )
                        Spacer(Modifier.weight(1f))
                        GhostIconButton(
                            icon = Icons.Default.Share,
                            contentDescription = "Share ${product.name}",
                            onClick = { },
                            background = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                        )
                        Spacer(Modifier.width(10.dp))
                        GhostIconButton(
                            icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favourites" else "Save to favourites",
                            onClick = onToggleFavorite,
                            tint = if (isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            background = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                        )
                    }
                }

                if (product.finishes.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        product.finishes.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == selectedFinish) 20.dp else 6.dp, 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == selectedFinish) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                    ),
                            )
                        }
                    }
                }
            }
        }

        // ---- Product information --------------------------------------------
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(22.dp))
                Text(
                    text = product.brand.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StarRow(rating = product.rating, reviewCount = product.reviewCount)
                    Spacer(Modifier.weight(1f))
                    PriceText(
                        cents = product.priceCents,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = PlayfairFamily,
                        ),
                    )
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(26.dp))

                // ---- Finish selector --------------------------------------------
                Text(
                    text = "FINISH",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    product.finishes.forEachIndexed { index, finish ->
                        FinishSwatch(
                            color = finish.tone.color(),
                            name = finish.name,
                            selected = index == selectedFinish,
                            onClick = { selectedFinish = index },
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = product.finishes.getOrNull(selectedFinish)?.name.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(26.dp))

                HighlightStrip(highlights = product.highlights)
                Spacer(Modifier.height(26.dp))
            }
        }

        // ---- Complete the set -------------------------------------------------
        if (pairedProduct != null) {
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "COMPLETE THE SET",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                MaterialTheme.shapes.medium,
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ProductImage(
                            product = pairedProduct,
                            modifier = Modifier.size(width = 68.dp, height = 84.dp),
                            shape = MaterialTheme.shapes.small,
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pairedProduct.kind.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = pairedProduct.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(5.dp))
                            PriceText(
                                cents = pairedProduct.priceCents,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        // Fixed-width action column: letting it size to content
                        // let the button overlap the price on narrow screens.
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.width(74.dp),
                        ) {
                            GoldButton(
                                text = "Add",
                                onClick = onAddPaired,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "View",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(PillShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { onOpenPaired(pairedProduct.id) }
                                    .padding(vertical = 6.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(26.dp))
                }
            }
        }

        // ---- Add to cart -------------------------------------------------------
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GoldButton(
                    text = if (quantityInCart > 0) {
                        "Add to cart · $quantityInCart in bag"
                    } else {
                        "Add to cart"
                    },
                    onClick = onAddToCart,
                    leadingIcon = Icons.Default.ShoppingCart,
                    modifier = Modifier.weight(1f),
                )
                GhostIconButton(
                    icon = Icons.Default.ShoppingCart,
                    contentDescription = "Add to cart",
                    onClick = onAddToCart,
                    size = 56.dp,
                    bordered = true,
                )
            }
        }
    }
}