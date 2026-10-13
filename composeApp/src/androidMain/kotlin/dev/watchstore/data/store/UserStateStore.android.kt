package dev.watchstore.data.store

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dev.watchstore.platform.ShareBridge

private const val FILE_NAME = "user_state"

actual fun createUserStateStore(): UserStateStore {
    val context = checkNotNull(ShareBridge.context) {
        "ShareBridge.context must be registered before the UI composes"
    }
    return DataStoreUserStateStore(
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(FILE_NAME) },
        ),
    )
}