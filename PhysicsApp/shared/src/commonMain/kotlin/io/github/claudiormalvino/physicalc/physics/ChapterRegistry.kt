package io.github.claudiormalvino.physicalc.physics

/**
 * The single place that knows which chapters exist.
 *
 * As you port more chapters from Python, add them to this list and the whole
 * UI (chapter list, detail screens, calculators) picks them up automatically.
 */
object ChapterRegistry {
    val all: List<PhysicsChapter> = listOf(
        Chapter3(),
        Chapter4(),
        Chapter5(),
        Chapter6(),
        Chapter7(),
        Chapter8(),
        Chapter9(),
        Chapter10(),
        Chapter11(),
        Chapter12(),
        Chapter13(),
        Chapter14(),
    )
}
