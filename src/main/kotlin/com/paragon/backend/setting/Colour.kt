package com.paragon.backend.setting

import java.awt.Color

/**
 * @author surge
 * @since 11/02/2023
 */
class Colour(r: Int, g: Int, b: Int, a: Int) : Color(r, g, b, a) {

    private val rainbow = false
    private val rainboxSpeed = 4f
    private val rainbowSaturation = 100f
    private val synced = false

    fun integrateAlpha(alpha: Int): Colour {
        return Colour(this.red, this.green, this.blue, alpha)
    }
}