package io.github.claudiormalvino.physicalc

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.backhandler.BackHandler
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import io.github.claudiormalvino.physicalc.physics.ChapterRegistry
import io.github.claudiormalvino.physicalc.ui.GlassBottomBar
import io.github.claudiormalvino.physicalc.ui.AppSettings
import io.github.claudiormalvino.physicalc.ui.CalculatorScreen
import io.github.claudiormalvino.physicalc.ui.ChapterDetailScreen
import io.github.claudiormalvino.physicalc.ui.ChaptersScreen
import io.github.claudiormalvino.physicalc.ui.HomeScreen
import io.github.claudiormalvino.physicalc.ui.PhysicsTheme
import io.github.claudiormalvino.physicalc.ui.SettingsSheet
import io.github.claudiormalvino.physicalc.ui.ToolsScreen
import io.github.claudiormalvino.physicalc.ui.UnitConverterScreen

/*
 * App entry point + navigation.
 *
 * Two layers:
 *  - Root tabs (bottom navigation bar): Tools | Home | Chapters. Home is the
 *    animated solar-system landing page.
 *  - A push stack of detail screens (chapter detail, solver, converter) that
 *    cover the tabs full-screen; the bottom bar hides while one is open.
 */

/** The three root tabs, in bar order. */
enum class RootTab(val label: String) {
    Tools("Tools"),
    Home("Home"),
    Chapters("Chapters"),
}

/** Detail screens pushed above the tabs. */
sealed interface Screen {
    data class ChapterDetail(val chapterIndex: Int) : Screen
    data class Calculator(val chapterIndex: Int, val equationIndex: Int) : Screen
    data object UnitConverter : Screen
}

/** How "deep" something is — used to slide forward vs. backward. */
private fun depth(state: Any): Int = when (state) {
    is RootTab -> 0
    is Screen.Calculator -> 2
    is Screen -> 1
    else -> 0
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {
    PhysicsTheme(darkTheme = AppSettings.darkTheme) {
        var tab by remember { mutableStateOf(RootTab.Home) }
        var stack by remember { mutableStateOf<List<Screen>>(emptyList()) }
        var showSettings by remember { mutableStateOf(false) }

        fun push(screen: Screen) { stack = stack + screen }
        fun pop() { stack = stack.dropLast(1) }

        // Android back: pop a detail screen first; from another tab go Home.
        BackHandler(enabled = stack.isNotEmpty() || tab != RootTab.Home) {
            if (stack.isNotEmpty()) pop() else tab = RootTab.Home
        }

        if (showSettings) {
            SettingsSheet(onDismiss = { showSettings = false })
        }

        // Current screen: the top of the push stack, or the active tab's root.
        val current: Any = stack.lastOrNull() ?: tab

        // Content renders edge-to-edge and feeds the glass bar's backdrop blur.
        val hazeState = remember { HazeState() }

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            AnimatedContent(
                targetState = current,
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState)
                    .background(MaterialTheme.colorScheme.background),
                transitionSpec = {
                    // Slide left when going deeper; between tabs, slide by bar order.
                    val from = initialState
                    val to = targetState
                    val forward = when {
                        depth(to) != depth(from) -> depth(to) > depth(from)
                        to is RootTab && from is RootTab -> to.ordinal > from.ordinal
                        else -> true
                    }
                    val enter = slideInHorizontally { full -> if (forward) full / 4 else -full / 4 } + fadeIn()
                    val exit = slideOutHorizontally { full -> if (forward) -full / 4 else full / 4 } + fadeOut()
                    enter togetherWith exit
                },
            ) { state ->
                when (state) {
                    RootTab.Home -> HomeScreen(onSettingsClick = { showSettings = true })

                    RootTab.Tools -> ToolsScreen(
                        onConverterClick = { push(Screen.UnitConverter) },
                    )

                    RootTab.Chapters -> ChaptersScreen(
                        onChapterClick = { index -> push(Screen.ChapterDetail(index)) },
                    )

                    is Screen.ChapterDetail -> ChapterDetailScreen(
                        chapter = ChapterRegistry.all[state.chapterIndex],
                        onOpenCalculator = { equationIndex ->
                            push(Screen.Calculator(state.chapterIndex, equationIndex))
                        },
                        onBack = ::pop,
                    )

                    is Screen.Calculator -> CalculatorScreen(
                        chapter = ChapterRegistry.all[state.chapterIndex],
                        equationIndex = state.equationIndex,
                        onBack = ::pop,
                    )

                    is Screen.UnitConverter -> UnitConverterScreen(onBack = ::pop)
                }
            }

            if (stack.isEmpty()) {
                GlassBottomBar(
                    selected = tab,
                    onSelect = { tab = it },
                    hazeState = hazeState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}
