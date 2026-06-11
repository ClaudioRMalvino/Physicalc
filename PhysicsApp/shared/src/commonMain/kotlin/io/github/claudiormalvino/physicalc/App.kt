package io.github.claudiormalvino.physicalc

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import io.github.claudiormalvino.physicalc.physics.ChapterRegistry
import io.github.claudiormalvino.physicalc.ui.CalculatorScreen
import io.github.claudiormalvino.physicalc.ui.ChapterDetailScreen
import io.github.claudiormalvino.physicalc.ui.ChapterListScreen
import io.github.claudiormalvino.physicalc.ui.PhysicsTheme
import io.github.claudiormalvino.physicalc.ui.UnitConverterScreen

/*
 * App entry point + navigation.
 *
 * Navigation here is deliberately simple: a back-stack held in plain Compose state.
 * `Screen` is a sealed interface, a fixed set of possible screens, which makes the
 * `when` below exhaustive (the compiler errors if you forget to handle one).
 * This is the Kotlin equivalent of how your Textual app pushed/popped Screens.
 */
sealed interface Screen {
    data object ChapterList : Screen
    data class ChapterDetail(val chapterIndex: Int) : Screen
    data class Calculator(val chapterIndex: Int, val equationIndex: Int) : Screen
    data object UnitConverter : Screen
}

/** How "deep" a screen is — used to slide forward vs. backward. */
private fun depth(screen: Screen): Int = when (screen) {
    is Screen.ChapterList -> 0
    is Screen.ChapterDetail -> 1
    is Screen.Calculator -> 2
    is Screen.UnitConverter -> 1
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {
    PhysicsTheme {
        // The back-stack: a list of screens, last = currently visible.
        var stack by remember { mutableStateOf<List<Screen>>(listOf(Screen.ChapterList)) }
        val current = stack.last()

        fun push(screen: Screen) { stack = stack + screen }
        fun pop() { if (stack.size > 1) stack = stack.dropLast(1) }

        // Android hardware/gesture back pops our stack instead of closing the app.
        BackHandler(enabled = stack.size > 1) { pop() }

        AnimatedContent(
            targetState = current,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            transitionSpec = {
                // Slide left when going deeper, right when going back.
                val forward = depth(targetState) >= depth(initialState)
                val enter = slideInHorizontally { full -> if (forward) full / 4 else -full / 4 } + fadeIn()
                val exit = slideOutHorizontally { full -> if (forward) -full / 4 else full / 4 } + fadeOut()
                enter togetherWith exit
            },
        ) { screen ->
            when (screen) {
                is Screen.ChapterList -> ChapterListScreen(
                    onChapterClick = { index -> push(Screen.ChapterDetail(index)) },
                    onConverterClick = { push(Screen.UnitConverter) },
                )

                is Screen.ChapterDetail -> ChapterDetailScreen(
                    chapter = ChapterRegistry.all[screen.chapterIndex],
                    onOpenCalculator = { equationIndex ->
                        push(Screen.Calculator(screen.chapterIndex, equationIndex))
                    },
                    onBack = ::pop,
                )

                is Screen.Calculator -> CalculatorScreen(
                    chapter = ChapterRegistry.all[screen.chapterIndex],
                    equationIndex = screen.equationIndex,
                    onBack = ::pop,
                )

                is Screen.UnitConverter -> UnitConverterScreen(onBack = ::pop)
            }
        }
    }
}
