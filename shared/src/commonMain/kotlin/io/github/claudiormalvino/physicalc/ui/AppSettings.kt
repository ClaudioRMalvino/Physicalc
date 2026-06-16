package io.github.claudiormalvino.physicalc.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings

/**
 * User preferences, persisted via multiplatform-settings (SharedPreferences / java.util.prefs).
 * `darkTheme` and `uiScale` are also Compose state, so changing them updates the whole app live.
 */
object AppSettings {
    private val store = Settings()
    private const val KEY_DARK_THEME = "dark_theme"
    private const val KEY_UI_SCALE = "ui_scale"

    /** User UI-scale multiplier (desktop). 1.0 = the auto-detected size; clamped to [MIN, MAX]. */
    const val UI_SCALE_MIN = 0.5f
    const val UI_SCALE_MAX = 2.0f

    var darkTheme: Boolean by mutableStateOf(store.getBoolean(KEY_DARK_THEME, true))
        private set

    var uiScale: Float by mutableFloatStateOf(
        store.getFloat(KEY_UI_SCALE, 1.0f).coerceIn(UI_SCALE_MIN, UI_SCALE_MAX),
    )
        private set

    fun updateDarkTheme(enabled: Boolean) {
        darkTheme = enabled
        store.putBoolean(KEY_DARK_THEME, enabled)
    }

    fun updateUiScale(scale: Float) {
        val clamped = scale.coerceIn(UI_SCALE_MIN, UI_SCALE_MAX)
        uiScale = clamped
        store.putFloat(KEY_UI_SCALE, clamped)
    }
}

/** Static app metadata shown in the About section. */
object AppInfo {
    const val NAME = "Physicalc"
    const val VERSION = "1.0"
    const val LICENSE = "MIT License"
    const val REPO_URL = "https://github.com/ClaudioRMalvino/Physicalc"
}
