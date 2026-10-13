package dev.watchstore.data.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Durable storage for the two pieces of state that must outlive navigation and
 * process death: the bag and favourites.
 *
 * Keys are namespaced rather than nested, because Preferences has no map type.
 * Reads filter the whole key set by prefix.
 */
interface UserStateStore {
    val cart: Flow<Map<String, Int>>
    val favorites: Flow<Set<String>>

    suspend fun saveCart(cart: Map<String, Int>)
    suspend fun saveFavorites(favorites: Set<String>)
}

/**
 * Preferences files are flat maps, so the bag is stored one entry per product
 * id under a prefix. Reads filter the whole key set by prefix.
 */
private const val CART_PREFIX = "cart."
private const val FAVORITE_PREFIX = "fav."

class DataStoreUserStateStore(
    private val dataStore: DataStore<Preferences>,
) : UserStateStore {

    override val cart: Flow<Map<String, Int>> = dataStore.data.map { prefs ->
        prefs.asMap()
            .filterKeys { it.name.startsWith(CART_PREFIX) }
            .mapKeys { it.key.name.removePrefix(CART_PREFIX) }
            .mapValues { (_, value) -> (value as? Int) ?: 0 }
            .filterValues { it > 0 }
    }

    override val favorites: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs.asMap()
            .keys
            .filter { it.name.startsWith(FAVORITE_PREFIX) }
            .map { it.name.removePrefix(FAVORITE_PREFIX) }
            .toSet()
    }

    override suspend fun saveCart(cart: Map<String, Int>) {
        dataStore.edit { prefs ->
            prefs.asMap().keys
                .filter { it.name.startsWith(CART_PREFIX) }
                .forEach { prefs.remove(it) }
            cart.filterValues { it > 0 }.forEach { (id, quantity) ->
                prefs[intPreferencesKey(CART_PREFIX + id)] = quantity
            }
        }
    }

    override suspend fun saveFavorites(favorites: Set<String>) {
        dataStore.edit { prefs ->
            prefs.asMap().keys
                .filter { it.name.startsWith(FAVORITE_PREFIX) }
                .forEach { prefs.remove(it) }
            favorites.forEach { id ->
                prefs[stringPreferencesKey(FAVORITE_PREFIX + id)] = ""
            }
        }
    }
}

/**
 * Builds the store for the current platform.
 *
 * Only an Android actual exists because iOS is not compiled yet; add an
 * `iosMain` actual (Document/DataStore on the app support directory) when the
 * target is enabled. `MainActivity` must register `ShareBridge.context` before
 * the UI composes.
 */
expect fun createUserStateStore(): UserStateStore