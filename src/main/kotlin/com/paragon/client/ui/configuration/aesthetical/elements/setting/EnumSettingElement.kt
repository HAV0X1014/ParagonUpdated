package com.paragon.client.ui.configuration.aesthetical.elements.setting

import com.paragon.backend.setting.Setting
import com.paragon.client.ui.configuration.aesthetical.elements.SettingElement
import com.paragon.util.formatCapitalised
import com.paragon.util.rendering.NVGWrapper
import me.surge.animation.BoundedAnimation
import me.surge.animation.Easing
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2
import java.awt.Color

/**
 * @author aesthetical
 * @since 02/24/23
 */
class EnumSettingElement(private val setting: Setting<Enum<*>>) : SettingElement() {
    private val hoverAnimation = BoundedAnimation(0.0f, 2.0f, 20.0f, false, Easing.LINEAR)

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        hoverAnimation.state = hovered(mouseX, mouseY, h = height * 2)

        nvg.quad(x, y, width, height * 2, SETTING_BG)

        val midPoint = (((height * 2) / 2.0f) - 16.0f / 2.0f)
        nvg.text(setting.name, x + 4.0f + (hoverAnimation.animationFactor.toFloat() * 2.0f), y + midPoint, Color.WHITE, "axiforma", shadow = true)
        val t = setting.value.toString().formatCapitalised()
        nvg.text(t, x + width - (nvg.textWidth(t, face = "axiforma", size = 16.0f) + 4.0f), y + midPoint, Color.GRAY, "axiforma", shadow = true)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {

        if (hovered(mouseX, mouseY, h = height * 2)) {
            if (button == GLFW_MOUSE_BUTTON_1) {
                setting.setValue(setting.nextEnum)
            } else if (button == GLFW_MOUSE_BUTTON_2) {
                setting.setValue(setting.previousEnum)
            }
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

    companion object {
        private val SETTING_BG = Color(19, 19, 19)
    }
}