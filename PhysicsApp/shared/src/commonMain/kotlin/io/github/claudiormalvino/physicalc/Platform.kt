package io.github.claudiormalvino.physicalc

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform