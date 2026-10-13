package dev.watchstore.feature.store

import androidx.lifecycle.ViewModel
import dev.watchstore.data.model.CartLine
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.ProductKind
import dev.watchstore.data.repository.ProductRepository
import dev.watchstore.data.repository.SeedProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * App-scoped state. Everything here is synchronous and small, so the view model
 * does no actual loading — it just owns the cart and favourites that outlive
 * navigation.
 *
 * Persistence (DataStore) is deliberately deferred; see PLAN.md.
 */
class StoreViewModel(
    private val repository: ProductRepository = SeedProductRepository(),
) : ViewModel() {

    private val _cart = MutableStateFlow<Map<String, Int>>(emptyMap())
    val cart: StateFlow<Map<String, Int>> = _cart.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    val heroes = repository.heroes
    val featured: List<Product> = repository.featured()
    val popular: List<Product> = repository.popular()

    fun product(id: String): Product? = repository.byId(id)
    fun byKind(kind: ProductKind): List<Product> = repository.byKind(kind)
    fun search(query: String): List<Product> = repository.search(query)

    fun products(): List<Product> = repository.products()

    fun favoriteProducts(): List<Product> =
        _favorites.value.mapNotNull(repository::byId)

    fun cartCount(): Int = _cart.value.values.sum()

    fun cartLines(): List<CartLine> = _cart.value.mapNotNull { (id, qty) ->
        repository.byId(id)?.let { CartLine(it, qty) }
    }

    fun cartSubtotalCents(): Long = cartLines().sumOf { it.product.priceCents * it.quantity }

    fun addToCart(id: String) {
        _cart.update { it + (id to ((it[id] ?: 0) + 1)) }
    }

    fun setQuantity(id: String, quantity: Int) {
        _cart.update { current ->
            if (quantity <= 0) current - id
            else current + (id to quantity)
        }
    }

    fun toggleFavorite(id: String) {
        _favorites.update { if (id in it) it - id else it + id }
    }

    fun isFavorite(id: String): Boolean = id in _favorites.value
}