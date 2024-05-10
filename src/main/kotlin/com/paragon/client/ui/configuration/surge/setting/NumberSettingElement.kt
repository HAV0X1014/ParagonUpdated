package com.paragon.client.ui.configuration.surge.setting

import com.paragon.backend.setting.Setting
import com.paragon.util.calculations.MathsUtil.roundDouble
import com.paragon.util.getClientColour
import com.paragon.util.rendering.NVGWrapper
import java.awt.Color
import kotlin.math.roundToInt

/**
 * @author surge
 * @since 11/02/2023
 */
class NumberSettingElement(setting: Setting<Number>, x: Float, y: Float, width: Float, height: Float) : SettingElement<Number>(setting, x, y, width, height) {

    private var dragging = false

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        nvg.quad(x, y, width, height, if (hovered(mouseX, mouseY)) hovered else background)

        val difference = width.coerceAtMost(0f.coerceAtLeast(mouseX - (x + 4))).toDouble()

        val minimum = setting.minimum!!.toDouble()
        val maximum = setting.maximum!!.toDouble()

        val renderWidth = ((width - 8) * (setting.value.toDouble() - minimum) / (maximum - minimum)).toFloat()

        val value: Double

        if (dragging) {
            value = if (difference == 0.0) {
                minimum
            } else {
                val newValue = roundDouble(difference / (width - 8) * (maximum - minimum) + minimum, 2)
                val precision = 1 / setting.incrementation!!.toDouble()
                (minimum.coerceAtLeast(maximum.coerceAtMost(newValue)) * precision).roundToInt() / precision
            }

            when (setting.value) {
                is Int -> setting.setValue(value.toInt())
                is Float -> setting.setValue(value.toFloat())
                is Double -> setting.setValue(value)
            }
        }

        nvg.quad(x + 4, y + 24, width - 8f, 2f, Color(20, 20, 20))
        nvg.quad(x + 4, y + 24, renderWidth, 2f, getClientColour())
        nvg.quad(x + 2 + renderWidth, y + 23, 4f, 4f, Color.WHITE)

        nvg.text(setting.name, x + 5, y + 7, Color.WHITE, size = 12f, shadow = false)
        nvg.text(setting.value.toString(), x + width - nvg.textWidth(setting.value.toString(), size = 12.0f) - 5, y + 7, Color.GRAY, size = 12f, shadow = false)

        super.render(nvg, mouseX, mouseY, mouseDelta)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY) && button == 0) {
            dragging = true
            return true
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

    override fun mouseRelease(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (dragging) {
            dragging = false
            return true
        }

        return super.mouseRelease(mouseX, mouseY, button)
    }

}