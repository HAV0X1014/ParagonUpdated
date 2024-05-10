package com.paragon.client.ui.configuration.aesthetical.elements.setting

import com.paragon.backend.setting.Setting
import com.paragon.client.ui.configuration.aesthetical.elements.SettingElement
import com.paragon.util.calculations.MathsUtil
import com.paragon.util.getClientColour
import com.paragon.util.rendering.NVGWrapper
import me.surge.animation.BoundedAnimation
import me.surge.animation.Easing
import java.awt.Color
import kotlin.math.roundToInt

/**
 * @author aesthetical
 * @since 02/24/23
 */
class NumberSettingElement(private val setting: Setting<Number>) : SettingElement() {
    private val hoverAnimation = BoundedAnimation(0.0f, 2.0f, 20.0f, false, Easing.LINEAR)
    private var dragging = false

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        hoverAnimation.state = hovered(mouseX, mouseY, h = height * 2)

        nvg.quad(x, y, width, height * 2, SETTING_BG)

        val difference = width.coerceAtMost(0f.coerceAtLeast(mouseX - (x + 4))).toDouble()

        val minimum = setting.minimum!!.toDouble()
        val maximum = setting.maximum!!.toDouble()

        val value: Double

        if (dragging) {
            value = if (difference == 0.0) {
                minimum
            } else {
                val newValue = MathsUtil.roundDouble(difference / (width - 8) * (maximum - minimum) + minimum, 2)
                val precision = 1 / setting.incrementation!!.toDouble()
                (minimum.coerceAtLeast(maximum.coerceAtMost(newValue)) * precision).roundToInt() / precision
            }

            when (setting.value) {
                is Int -> setting.setValue(value.toInt())
                is Float -> setting.setValue(value.toFloat())
                is Double -> setting.setValue(value)
            }
        }

        val renderWidth = ((width - 4) * (setting.value.toDouble() - minimum) / (maximum - minimum)).toFloat()
        nvg.quad(x, y, renderWidth, height * 2, getClientColour())

        val midPoint = (((height * 2) / 2.0f) - 16.0f / 2.0f)
        nvg.text(setting.name, x + 4.0f + (hoverAnimation.animationFactor.toFloat() * 2.0f), y + midPoint, Color.WHITE, "axiforma", shadow = true)

        val t = setting.value.toString()
        nvg.text(t, x + width - (nvg.textWidth(t, face = "axiforma", size = 16.0f) + 4.0f), y + midPoint, Color.GRAY, "axiforma", shadow = true)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY, h = height * 2) && button == 0) {
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

    companion object {
        private val SETTING_BG = Color(19, 19, 19)
    }
}