package com.paragon.client.ui.configuration.aesthetical.elements.setting

import com.paragon.backend.setting.Setting
import com.paragon.client.ui.configuration.aesthetical.elements.SettingElement
import com.paragon.util.getClientColour
import com.paragon.util.rendering.NVGWrapper
import me.surge.animation.BoundedAnimation
import me.surge.animation.Easing
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1
import java.awt.Color

/**
 * @author aesthetical
 * @since 02/23/23
 */
class BooleanSettingElement(private val setting: Setting<Boolean>) : SettingElement() {
    private val hoverAnimation = BoundedAnimation(0.0f, 2.0f, 20.0f, false, Easing.LINEAR)

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        hoverAnimation.state = hovered(mouseX, mouseY, h = height * 2)

        nvg.quad(x, y, width, height * 2, SETTING_BG)

        val midPoint = (((height * 2) / 2.0f) - 16.0f / 2.0f)
        nvg.text(setting.name, x + 4.0f + (hoverAnimation.animationFactor.toFloat() * 2.0f), y + midPoint, Color.WHITE, "axiforma", shadow = true)

        val dimension = ((height - 4) * 2)
        nvg.roundedQuad(x + width - (dimension + 4.0f), y + midPoint, dimension, dimension, 2.5f, if (setting.value) getClientColour() else SETTING_BG.brighter())
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {

        if (hovered(mouseX, mouseY, h = height * 2) && button == GLFW_MOUSE_BUTTON_1) {
            setting.setValue(!setting.value)
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

    companion object {
        private val SETTING_BG = Color(19, 19, 19)
    }
}