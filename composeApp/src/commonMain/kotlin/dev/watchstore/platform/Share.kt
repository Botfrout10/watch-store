package dev.watchstore.platform

/**
 * Opens the platform share sheet.
 *
 * `expect`/`actual` keeps the shared UI free of Android or iOS types. Only an
 * Android actual exists today because iOS is not compiled yet — add an
 * `iosMain` actual (UIActivityViewController) when the target is enabled.
 */
expect fun shareContent(subject: String, text: String)