package com.paragon.util.calculations

import java.util.function.Function

/**
 * @author surge
 * @since 11/02/2023
 */
class Timer {

    private var last: Long

    init {
        last = System.currentTimeMillis()
    }

    fun elapsed(delay: Double, format: Format = Format.MILLISECONDS): Boolean {
        return timeMs() >= format.mutate(delay)
    }

    fun reset() {
        last = System.currentTimeMillis()
    }

    fun timeMs(): Long {
        return System.currentTimeMillis() - last
    }

    enum class Format(val mutate: (Double) -> Double) {
        MILLISECONDS({ it }),
        SECONDS({ it * 1000 })
    }

}