package io.github.claudiormalvino.physicalc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Physicalc",
    ) {
        App()
    }
}