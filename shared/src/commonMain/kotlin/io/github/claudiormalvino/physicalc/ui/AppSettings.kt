package io.github.claudiormalvino.physicalc.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings

/**
 * User preferences, persisted across launches.
 *
 * `Settings()` (multiplatform-settings) stores values in the platform's native
 * key-value store: SharedPreferences on Android, java.util.prefs on desktop,
 * NSUserDefaults on iOS — no platform glue needed here.
 *
 * `darkTheme` is also Compose state (`mutableStateOf`), so flipping it instantly
 * recomposes everything that reads it — the whole app retints live.
 */
object AppSettings {
    private val store = Settings()
    private const val KEY_DARK_THEME = "dark_theme"

    var darkTheme: Boolean by mutableStateOf(store.getBoolean(KEY_DARK_THEME, true))
        private set

    fun updateDarkTheme(enabled: Boolean) {
        darkTheme = enabled
        store.putBoolean(KEY_DARK_THEME, enabled)
    }
}

/** Static app metadata shown in the About section. */
object AppInfo {
    const val NAME = "Physicalc"
    const val VERSION = "1.0"
    const val LICENSE = "MIT License"
    const val REPO_URL = "https://github.com/ClaudioRMalvino/Physicalc"
}
