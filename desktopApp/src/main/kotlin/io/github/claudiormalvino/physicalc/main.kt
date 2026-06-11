package io.github.claudiormalvino.physicalc

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Physicalc",
        // Phone-like default proportions; the layout also caps content width,
        // so maximizing the window stays readable instead of stretching.
        state = rememberWindowState(width = 480.dp, height = 920.dp),
    ) {
        App()
    }
}
