package dev.watchstore.data.repository

import dev.watchstore.data.catalog.galleryFor
import dev.watchstore.data.catalog.seedHeroes
import dev.watchstore.data.catalog.seedProducts
import dev.watchstore.data.model.Hero
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.ProductKind

/** Where the catalogue in a [CatalogSnapshot] came from. */
enum class CatalogSource {
    /** Fetched over HTTP. */
    REMOTE,

    /** Built in from the compiled-in seed. */
    SEED,
}

/**
 * The whole catalogue in one value.
 *
 * Fetching is a single call rather than a dozen query methods because a remote
 * source cannot answer `byId` synchronously — and pretending otherwise is what
 * pushes `suspend` into every screen. The store loads once and derives.
 */
data class CatalogSnapshot(
    val products: List<Product>,
    val heroes: List<Hero>,
    val source: CatalogSource,
)

// ---- Derivation ------------------------------------------------------------
// Pure functions over a loaded snapshot; no I/O, safe to call from the UI.

fun CatalogSnapshot.byId(id: String): Product? = products.firstOrNull { it.id == id }

fun CatalogSnapshot.byKind(kind: ProductKind): List<Product> = products.filter { it.kind == kind }

fun CatalogSnapshot.featured(): List<Product> = products.filter { it.isFeatured }

fun CatalogSnapshot.popular(): List<Product> = products.filter { it.isPopular }

fun CatalogSnapshot.favoriteProducts(ids: Set<String>): List<Product> =
    ids.mapNotNull { byId(it) }

fun CatalogSnapshot.cartLines(quantities: Map<String, Int>): List<dev.watchstore.data.model.CartLine> =
    quantities.mapNotNull { (id, qty) -> byId(id)?.let { dev.watchstore.data.model.CartLine(it, qty) } }

fun CatalogSnapshot.search(query: String): List<Product> {
    val q = query.trim().lowercase()
    if (q.isEmpty()) return products
    return products.filter {
        q in it.name.lowercase() ||
            q in it.brand.lowercase() ||
            q in it.tagline.lowercase() ||
            q in it.kind.label.lowercase()
    }
}

/**
 * The single seam between the UI and whatever backs the catalogue.
 *
 * Nothing above this interface knows whether data came from the network or the
 * compiled-in seed, which is what makes the offline fallback invisible to every
 * screen.
 */
interface ProductRepository {
    suspend fun loadCatalog(): CatalogSnapshot
}

class SeedProductRepository : ProductRepository {
    override suspend fun loadCatalog(): CatalogSnapshot = CatalogSnapshot(
        products = seedProducts,
        heroes = seedHeroes,
        source = CatalogSource.SEED,
    )
}

/**
 * Tries [primary] and falls back to [secondary] if it fails.
 *
 * A retail app that shows an empty catalogue because the network blipped is
 * worse than one that shows slightly stale stock, so the seed always answers.
 * Whether the fallback actually fired is reported in the snapshot rather than
 * swallowed.
 */
class FallbackProductRepository(
    private val primary: ProductRepository,
    private val secondary: ProductRepository,
    private val onFallback: (Throwable) -> Unit = {},
) : ProductRepository {
    override suspend fun loadCatalog(): CatalogSnapshot =
        try {
            primary.loadCatalog()
        } catch (t: Throwable) {
            onFallback(t)
            secondary.loadCatalog()
        }
}