package dev.watchstore.data.repository

import dev.watchstore.data.catalog.seedHeroes
import dev.watchstore.data.catalog.seedProducts
import dev.watchstore.data.model.Hero
import dev.watchstore.data.model.Product
import dev.watchstore.data.model.ProductKind

/**
 * The single seam between the UI and whatever backs the catalogue.
 *
 * v1 is seeded in Kotlin. Swapping in a Ktor-backed implementation later means
 * providing a different `ProductRepository` — no composable, view model or model
 * changes, because nothing above this interface knows where products come from.
 */
interface ProductRepository {
    val heroes: List<Hero>
    fun products(): List<Product>
    fun byId(id: String): Product?
    fun byKind(kind: ProductKind): List<Product>
    fun featured(): List<Product>
    fun popular(): List<Product>
    fun search(query: String): List<Product>
}

class SeedProductRepository : ProductRepository {

    private val all: List<Product> = seedProducts

    override val heroes: List<Hero> = seedHeroes

    override fun products(): List<Product> = all

    override fun byId(id: String): Product? = all.firstOrNull { it.id == id }

    override fun byKind(kind: ProductKind): List<Product> = all.filter { it.kind == kind }

    override fun featured(): List<Product> = all.filter { it.isFeatured }

    override fun popular(): List<Product> = all.filter { it.isPopular }

    override fun search(query: String): List<Product> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return all
        return all.filter {
            q in it.name.lowercase() ||
                q in it.brand.lowercase() ||
                q in it.tagline.lowercase() ||
                q in it.kind.label.lowercase()
        }
    }
}