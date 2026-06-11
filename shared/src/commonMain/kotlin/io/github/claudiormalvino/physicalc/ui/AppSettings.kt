package io.github.claudiormalvino.physicalc.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings

/**
 * User preferences, persisted via multiplatform-settings (SharedPreferences / java.util.prefs).
 * `darkTheme` is also Compose state, so toggling it retints the whole app live.
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
