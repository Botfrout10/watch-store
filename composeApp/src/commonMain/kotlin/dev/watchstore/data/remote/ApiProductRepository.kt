package dev.watchstore.data.remote

import dev.watchstore.data.repository.CatalogSnapshot
import dev.watchstore.data.repository.CatalogSource
import dev.watchstore.data.repository.ProductRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

/**
 * Fetches the catalogue over HTTP.
 *
 * Throws on any failure; [dev.watchstore.data.repository.FallbackProductRepository]
 * turns that into the compiled-in seed, so callers never need a try/catch.
 */
class ApiProductRepository(
    private val client: HttpClient,
    private val baseUrl: String,
    private val path: String = DEFAULT_PATH,
) : ProductRepository {

    override suspend fun loadCatalog(): CatalogSnapshot {
        val response = client.get(baseUrl.trimEnd('/') + path)

        if (response.status != HttpStatusCode.OK) {
            throw ApiException("Catalog request failed: ${response.status}")
        }

        val dto: CatalogResponse = response.body()
        val (products, heroes) = dto.toDomain()

        if (products.isEmpty()) {
            throw ApiException("Catalog returned no products")
        }

        return CatalogSnapshot(products, heroes, CatalogSource.REMOTE)
    }

    companion object {
        const val DEFAULT_PATH = "/api/v1/catalog"
    }
}

class ApiException(message: String) : Exception(message)