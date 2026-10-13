package dev.watchstore.feature.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.watchstore.data.model.CartLine
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.ProductKind
import dev.watchstore.data.repository.ProductRepository
import dev.watchstore.data.repository.SeedProductRepository
import dev.watchstore.data.store.UserStateStore
import dev.watchstore.data.store.createUserStateStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * App-scoped state.
 *
 * The bag and favourites come straight from [UserStateStore] rather than being
 * mirrored into in-memory flows: persistence is the single source of truth, so
 * there is no chance of the UI and the disk disagreeing after a failed write.
 */
class StoreViewModel(
    private val repository: ProductRepository = SeedProductRepository(),
    private val store: UserStateStore = createUserStateStore(),
) : ViewModel() {

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

    val heroes = repository.heroes
    val featured: List<Product> = repository.featured()
    val popular: List<Product> = repository.popular()

    fun product(id: String): Product? = repository.byId(id)
    fun byKind(kind: ProductKind): List<Product> = repository.byKind(kind)
    fun search(query: String): List<Product> = repository.search(query)

    fun products(): List<Product> = repository.products()

    fun favoriteProducts(): List<Product> =
        favorites.value.mapNotNull(repository::byId)

    fun cartCount(): Int = cart.value.values.sum()

    fun cartLines(): List<CartLine> =
        cart.value.mapNotNull { (id, qty) -> repository.byId(id)?.let { CartLine(it, qty) } }

    fun cartSubtotalCents(): Long = cartLines().sumOf { it.product.priceCents * it.quantity }

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
}