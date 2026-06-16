package io.github.claudiormalvino.physicalc

interface Platform {
    val name: String

    /** True on desktop (JVM), where the user controls UI scale; false on Android. */
    val isDesktop: Boolean
}

expect fun getPlatform(): Platform