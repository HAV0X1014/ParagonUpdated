package com.paragon.util.rendering

import java.awt.Color
import kotlin.math.abs

/**
 * @author aesthetical, linustouchtips
 * @since 02/17/23
 */
object ColorUtil {

    /**
     * Creates a color gradient with HSB by cycling through the brightness
     * (Credit to cosmos)
     * @param c the base color
     * @param min the minimum brightness value
     * @param delay the delay between switching
     * @return a color (brightness) gradient
     */
    fun gradientRainbow(c: Color, min: Float, delay: Int): Color {
        val hsb = Color.RGBtoHSB(c.red, c.green, c.blue, null)
        var brightness = abs(((System.currentTimeMillis() % 2000L).toFloat() / 1000.0f + 50.0f / delay.toFloat() * 2.0f) % 2.0f - 1.0f)
        brightness = (min + (1.0f - min) * brightness) % 2.0f
        return Color.getHSBColor(hsb[0], hsb[1], brightness)
    }

}