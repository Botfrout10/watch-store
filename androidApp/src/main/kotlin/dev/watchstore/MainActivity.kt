package dev.watchstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.watchstore.app.WatchStoreApp
import dev.watchstore.platform.ShareBridge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ShareBridge.context = applicationContext
        setContent {
            WatchStoreApp()
        }
    }
}