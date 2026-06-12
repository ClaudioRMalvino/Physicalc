package io.github.claudiormalvino.physicalc

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import io.github.claudiormalvino.physicalc.ui.FlashcardMode
import io.github.claudiormalvino.physicalc.ui.FlashcardsChaptersScreen
import io.github.claudiormalvino.physicalc.ui.FlashcardsModeScreen
import io.github.claudiormalvino.physicalc.ui.FlashcardsSessionScreen
import io.github.claudiormalvino.physicalc.ui.HelpOverlay
import io.github.claudiormalvino.physicalc.ui.HelpStore
import io.github.claudiormalvino.physicalc.ui.HelpTopic
import io.github.claudiormalvino.physicalc.ui.HomeScreen
import io.github.claudiormalvino.physicalc.ui.PhysicsTheme
import io.github.claudiormalvino.physicalc.ui.SettingsSheet
import io.github.claudiormalvino.physicalc.ui.ToolsScreen
import io.github.claudiormalvino.physicalc.ui.UnitConverterScreen
import io.github.claudiormalvino.physicalc.ui.VectorToolScreen
import kotlinx.coroutines.launch

/*
 * App entry point: root tabs (Tools | Home | Chapters) in a swipeable pager,
 * plus a push stack of full-screen detail screens; the bottom bar hides while
 * one is open. Tabs are reachable by bar tap or horizontal swipe.
 */

/** The three root tabs, in bar (and pager) order. */
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
    data object VectorTool : Screen
    data object FlashcardsMode : Screen
    data class FlashcardsChapters(val mode: FlashcardMode) : Screen
    data class FlashcardsSession(val mode: FlashcardMode, val chapterIndex: Int, val dueOnly: Boolean) : Screen
}

/** AnimatedContent state for the tab pager (the stack-empty case). */
private data object TabsRoot

/** The guide for a root tab; the welcome overview doubles as Home's guide. */
private fun helpTopicFor(tab: RootTab): HelpTopic = when (tab) {
    RootTab.Home -> HelpTopic.Welcome
    RootTab.Tools -> HelpTopic.Tools
    RootTab.Chapters -> HelpTopic.Chapters
}

/** How "deep" something is — used to slide forward vs. backward. */
private fun depth(state: Any): Int = when (state) {
    TabsRoot -> 0
    is Screen.Calculator -> 2
    is Screen.FlashcardsChapters -> 2
    is Screen.FlashcardsSession -> 3
    is Screen -> 1
    else -> 0
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() {
    PhysicsTheme(darkTheme = AppSettings.darkTheme) {
        var stack by remember { mutableStateOf<List<Screen>>(emptyList()) }
        var showSettings by remember { mutableStateOf(false) }

        // Fresh installs open onto the welcome guide; tab guides follow on first visit.
        var activeHelp by remember {
            mutableStateOf(if (HelpStore.seen(HelpTopic.Welcome)) null else HelpTopic.Welcome)
        }

        // Hoisted above AnimatedContent so the selected tab survives detail pushes.
        val pagerState = rememberPagerState(initialPage = RootTab.Home.ordinal) { RootTab.entries.size }
        val scope = rememberCoroutineScope()

        fun push(screen: Screen) { stack = stack + screen }
        fun pop() { stack = stack.dropLast(1) }
        fun goToTab(tab: RootTab) {
            scope.launch { pagerState.animateScrollToPage(tab.ordinal) }
        }

        // Android back: pop a detail screen first; from another tab go Home.
        BackHandler(enabled = stack.isNotEmpty() || pagerState.currentPage != RootTab.Home.ordinal) {
            if (stack.isNotEmpty()) pop() else goToTab(RootTab.Home)
        }

        // Auto-open a tab's guide the first time the user lands on it.
        LaunchedEffect(pagerState.settledPage) {
            val topic = helpTopicFor(RootTab.entries[pagerState.settledPage])
            if (topic != HelpTopic.Welcome && activeHelp == null && !HelpStore.seen(topic)) {
                activeHelp = topic
            }
        }

        // Likewise for tools with a guide of their own.
        LaunchedEffect(stack) {
            val topic = when (stack.lastOrNull()) {
                is Screen.VectorTool -> HelpTopic.Vectors
                is Screen.FlashcardsMode -> HelpTopic.Flashcards
                else -> null
            }
            if (topic != null && activeHelp == null && !HelpStore.seen(topic)) {
                activeHelp = topic
            }
        }

        // The guide for whatever is on screen; the flashcards guide covers its whole flow.
        fun helpTopicForContext(): HelpTopic = when (stack.lastOrNull()) {
            is Screen.VectorTool -> HelpTopic.Vectors
            is Screen.FlashcardsMode, is Screen.FlashcardsChapters, is Screen.FlashcardsSession ->
                HelpTopic.Flashcards
            else -> helpTopicFor(RootTab.entries[pagerState.currentPage])
        }

        val current: Any = stack.lastOrNull() ?: TabsRoot

        // Feeds the glass bar's backdrop blur.
        val hazeState = remember { HazeState() }

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            AnimatedContent(
                targetState = current,
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState)
                    .background(MaterialTheme.colorScheme.background),
                transitionSpec = {
                    // Slide left when going deeper, right when popping back out.
                    val forward = depth(targetState) > depth(initialState)
                    val enter = slideInHorizontally { full -> if (forward) full / 4 else -full / 4 } + fadeIn()
                    val exit = slideOutHorizontally { full -> if (forward) -full / 4 else full / 4 } + fadeOut()
                    enter togetherWith exit
                },
            ) { state ->
                when (state) {
                    TabsRoot -> HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        // Cap content width on wide windows; the Home solar system stays full-bleed.
                        val tab = RootTab.entries[page]
                        val frameModifier = if (tab == RootTab.Home) {
                            Modifier.fillMaxSize()
                        } else {
                            Modifier.fillMaxHeight().widthIn(max = 640.dp)
                        }
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                            Box(modifier = frameModifier) {
                                when (tab) {
                                    RootTab.Home -> HomeScreen(onSettingsClick = { showSettings = true })

                                    RootTab.Tools -> ToolsScreen(
                                        onConverterClick = { push(Screen.UnitConverter) },
                                        onFlashcardsClick = { push(Screen.FlashcardsMode) },
                                        onVectorsClick = { push(Screen.VectorTool) },
                                        onSettingsClick = { showSettings = true },
                                    )

                                    RootTab.Chapters -> ChaptersScreen(
                                        onChapterClick = { index -> push(Screen.ChapterDetail(index)) },
                                        onSettingsClick = { showSettings = true },
                                    )
                                }
                            }
                        }
                    }

                    is Screen.ChapterDetail -> Frame {
                        ChapterDetailScreen(
                            chapter = ChapterRegistry.all[state.chapterIndex],
                            onOpenCalculator = { equationIndex ->
                                push(Screen.Calculator(state.chapterIndex, equationIndex))
                            },
                            onBack = ::pop,
                        )
                    }

                    is Screen.Calculator -> Frame {
                        CalculatorScreen(
                            chapter = ChapterRegistry.all[state.chapterIndex],
                            equationIndex = state.equationIndex,
                            onBack = ::pop,
                        )
                    }

                    is Screen.UnitConverter -> Frame { UnitConverterScreen(onBack = ::pop) }

                    is Screen.VectorTool -> Frame {
                        VectorToolScreen(
                            onBack = ::pop,
                            onSettingsClick = { showSettings = true },
                        )
                    }

                    is Screen.FlashcardsMode -> Frame {
                        FlashcardsModeScreen(
                            onSelect = { mode -> push(Screen.FlashcardsChapters(mode)) },
                            onSettingsClick = { showSettings = true },
                            onBack = ::pop,
                        )
                    }

                    is Screen.FlashcardsChapters -> Frame {
                        FlashcardsChaptersScreen(
                            mode = state.mode,
                            onChapterClick = { index, dueOnly ->
                                push(Screen.FlashcardsSession(state.mode, index, dueOnly))
                            },
                            onBack = ::pop,
                        )
                    }

                    is Screen.FlashcardsSession -> Frame {
                        FlashcardsSessionScreen(
                            mode = state.mode,
                            chapter = ChapterRegistry.all[state.chapterIndex],
                            dueOnly = state.dueOnly,
                            onExit = ::pop,
                        )
                    }
                }
            }

            if (stack.isEmpty()) {
                GlassBottomBar(
                    selected = RootTab.entries[pagerState.targetPage.coerceIn(0, RootTab.entries.lastIndex)],
                    onSelect = ::goToTab,
                    hazeState = hazeState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }

            // Frosted-glass settings panel: scrim fades, panel slides up from the bottom.
            AnimatedVisibility(
                visible = showSettings,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                SettingsSheet(
                    hazeState = hazeState,
                    onDismiss = { showSettings = false },
                    onHelp = {
                        showSettings = false
                        activeHelp = helpTopicForContext()
                    },
                )
            }

            // Frosted-glass guide above everything, blurring the screen behind it.
            activeHelp?.let { topic ->
                HelpOverlay(
                    topic = topic,
                    hazeState = hazeState,
                    onDismiss = {
                        HelpStore.markSeen(topic)
                        activeHelp = null
                    },
                )
            }
        }
    }
}

/** Width-capped centered frame shared by all detail screens. */
@Composable
private fun Frame(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(modifier = Modifier.fillMaxHeight().widthIn(max = 640.dp)) {
            content()
        }
    }
}
