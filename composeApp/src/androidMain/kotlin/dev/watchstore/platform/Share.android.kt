package dev.watchstore.platform

import android.content.Context
import android.content.Intent

/**
 * Holds the application context for sharing.
 *
 * `composeApp` cannot reach an Activity from shared code, so `MainActivity`
 * registers itself here on create.
 */
object ShareBridge {
    var context: Context? = null
}

actual fun shareContent(subject: String, text: String) {
    val ctx = ShareBridge.context ?: return

    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }

    val chooser = Intent.createChooser(send, subject).apply {
        // The bridge may hold the application context rather than an Activity.
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    ctx.startActivity(chooser)
}