package io.github.claudiormalvino.physicalc

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.claudiormalvino.physicalc.ui.AppSettings
import java.awt.Toolkit

/*
 * Desktop entry point.
 *
 * HiDPI handling: on some Linux setups the JDK fails to detect the display
 * scale and renders every window at density 1.0, leaving the UI tiny on 4K
 * panels. We derive a scale from the reported screen size and fold it into the
 * Compose density. The check is self-correcting: when the OS scale *is* applied,
 * the toolkit reports the smaller logical size, so the factor stays 1.0 and we
 * don't double up.
 */
fun main() {
    val screen = Toolkit.getDefaultToolkit().screenSize
    val scaleFactor = when {
        screen.height >= 2000 -> 2.0f   // 4K-class panel, scale not applied
        screen.height >= 1400 -> 1.5f   // 1440p-class
        else -> 1.0f
    }

    application {
        // A tall, phone-like window sized as a fraction of the screen; the dp
        // value resolves to the same pixel size whether or not OS scaling is on.
        val windowState = rememberWindowState(
            width = (screen.width * 0.42f).dp,
            height = (screen.height * 0.82f).dp,
        )
        Window(
            onCloseRequest = ::exitApplication,
            title = "Physicalc",
            state = windowState,
        ) {
            // Auto-detected scale, times the user's manual override (live via Settings).
            val base = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(
                    base.density * scaleFactor * AppSettings.uiScale,
                    base.fontScale,
                ),
            ) {
                App()
            }
        }
    }
}
