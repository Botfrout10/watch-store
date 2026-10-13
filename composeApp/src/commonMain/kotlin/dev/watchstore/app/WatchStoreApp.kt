package dev.watchstore.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.watchstore.core.designsystem.Gold
import dev.watchstore.core.designsystem.WatchStoreTheme

/**
 * Temporary root used to validate the toolchain, bundled fonts and theme.
 * Replaced by the real navigation shell once the build is green.
 */
@Composable
fun WatchStoreApp() {
    WatchStoreTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
        ) {
            Text("Watch Store", style = MaterialTheme.typography.displayMedium)
            Text(
                "Playfair Display",
                style = MaterialTheme.typography.headlineMedium,
                color = Gold,
            )
            Text(
                "Inter — golden sand on warm dark.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}