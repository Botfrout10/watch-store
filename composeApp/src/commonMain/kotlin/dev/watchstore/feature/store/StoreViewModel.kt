package dev.watchstore.feature.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.watchstore.data.model.CartLine
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.ProductKind
import dev.watchstore.data.remote.ApiProductRepository
import dev.watchstore.data.remote.createHttpClient
import dev.watchstore.data.remote.defaultApiBaseUrl
import dev.watchstore.data.repository.CatalogSnapshot
import dev.watchstore.data.repository.CatalogSource
import dev.watchstore.data.repository.FallbackProductRepository
import dev.watchstore.data.repository.ProductRepository
import dev.watchstore.data.repository.SeedProductRepository
import dev.watchstore.data.repository.byId
import dev.watchstore.data.repository.byKind
import dev.watchstore.data.repository.cartLines
import dev.watchstore.data.repository.featured
import dev.watchstore.data.repository.favoriteProducts
import dev.watchstore.data.repository.popular
import dev.watchstore.data.repository.search
import dev.watchstore.data.store.UserStateStore
import dev.watchstore.data.store.createUserStateStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * App-scoped state.
 *
 * The catalogue loads asynchronously and reports which source answered, so the
 * UI can tell the user when it fell back to the seed rather than silently
 * showing stale data.
 *
 * The bag and favourites come straight from [UserStateStore]: persistence is the
 * single source of truth, so the UI and disk cannot disagree after a failed
 * write.
 */
class StoreViewModel(
    private val repository: ProductRepository = defaultRepository(),
    private val store: UserStateStore = createUserStateStore(),
) : ViewModel() {

    // Explicit type parameter: `Loading` is a data object, so without this the flow
    // would infer MutableStateFlow<CatalogState.Loading> and reject Ready.
    private val _catalog = MutableStateFlow<CatalogState>(CatalogState.Loading)

    /** Loading, then Ready. The seed guarantees Ready always arrives. */
    val catalog: StateFlow<CatalogState> = _catalog.asStateFlow()

    val cart: StateFlow<Map<String, Int>> = store.cart.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyMap(),
    )

    val favorites: StateFlow<Set<String>> = store.favorites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptySet(),
    )

    init {
        viewModelScope.launch {
            _catalog.value = CatalogState.Ready(repository.loadCatalog())
        }
    }

    private val snapshot: CatalogSnapshot?
        get() = (_catalog.value as? CatalogState.Ready)?.snapshot

    // ---- Catalogue ----------------------------------------------------------

    val heroes get() = snapshot?.heroes.orEmpty()
    val featured: List<Product> get() = snapshot?.featured().orEmpty()
    val popular: List<Product> get() = snapshot?.popular().orEmpty()
    val products: List<Product> get() = snapshot?.products.orEmpty()

    fun product(id: String): Product? = snapshot?.byId(id)
    fun byKind(kind: ProductKind): List<Product> = snapshot?.byKind(kind).orEmpty()
    fun search(query: String): List<Product> = snapshot?.search(query).orEmpty()
    fun favoriteProducts(): List<Product> = snapshot?.favoriteProducts(favorites.value).orEmpty()

    /** Hero product lookup, used by the home carousel. */
    fun heroProducts(): Map<String, Product> =
        heroes.mapNotNull { hero -> product(hero.productId)?.let { hero.productId to it } }.toMap()

    // ---- Bag ----------------------------------------------------------------

    fun cartCount(): Int = cart.value.values.sum()
    fun cartLines(): List<CartLine> = snapshot?.cartLines(cart.value).orEmpty()

    fun addToCart(id: String) {
        val next = cart.value.toMutableMap()
        next[id] = (next[id] ?: 0) + 1
        viewModelScope.launch { store.saveCart(next) }
    }

    fun setQuantity(id: String, quantity: Int) {
        val next = cart.value.toMutableMap()
        if (quantity <= 0) next.remove(id) else next[id] = quantity
        viewModelScope.launch { store.saveCart(next) }
    }

    fun toggleFavorite(id: String) {
        val current = favorites.value
        val next = if (id in current) current - id else current + id
        viewModelScope.launch { store.saveFavorites(next) }
    }

    fun isFavorite(id: String): Boolean = id in favorites.value

    companion object {
        /**
         * Network first, seed always available.
         *
         * Kept in a companion so the ViewModel's constructor stays
         * injectable for tests without forcing every caller to build a client.
         */
        fun defaultRepository(): ProductRepository = FallbackProductRepository(
            primary = ApiProductRepository(createHttpClient(), defaultApiBaseUrl()),
            secondary = SeedProductRepository(),
        )
    }
}

sealed interface CatalogState {
    data object Loading : CatalogState

    data class Ready(val snapshot: CatalogSnapshot) : CatalogState

    val source: CatalogSource?
        get() = (this as? Ready)?.snapshot?.source

    val isReady: Boolean get() = this is Ready
}