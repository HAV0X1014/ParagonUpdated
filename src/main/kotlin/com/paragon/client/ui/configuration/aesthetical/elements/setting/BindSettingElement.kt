package com.paragon.client.ui.configuration.aesthetical.elements.setting

import com.paragon.backend.bind.Bind
import com.paragon.backend.bind.DeviceType
import com.paragon.backend.setting.Setting
import com.paragon.client.ui.configuration.aesthetical.elements.SettingElement
import com.paragon.util.rendering.NVGWrapper
import me.surge.animation.BoundedAnimation
import me.surge.animation.Easing
import org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2
import java.awt.Color

/**
 * @author aesthetical
 * @since 02/23/23
 */
class BindSettingElement(private val setting: Setting<Bind>) : SettingElement() {
    private val hoverAnimation = BoundedAnimation(0.0f, 2.0f, 20.0f, false, Easing.LINEAR)
    private var listening = false

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        hoverAnimation.state = hovered(mouseX, mouseY, h = height * 2)

        nvg.quad(x, y, width, height * 2, SETTING_BG)

        val midPoint = (((height * 2) / 2.0f) - 16.0f / 2.0f)
        nvg.text(if (listening) "Listening..." else setting.name, x + 4.0f + (hoverAnimation.animationFactor.toFloat() * 2.0f), y + midPoint, Color.WHITE, "axiforma", shadow = true)
        if (!listening) {
            val text = setting.value.toString()
            nvg.text(text, x + width - (nvg.textWidth(text, face = "axiforma", size = 16.0f) + 4.0f), y + midPoint, Color.GRAY, "axiforma", shadow = true)
        }
    }

    override fun keyTyped(keyCode: Int, scanCode: Int, modifiers: Int) {

        if (listening) {
            listening = false
            setting.value.type = DeviceType.KEYBOARD
            setting.value.code = keyCode
        }

        super.keyTyped(keyCode, scanCode, modifiers)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY, h = height * 2) && button == 0) {
            listening = !listening
            return true // wait next mouse tick
        }

        // this is so we can attack/place without binds interfering
        // if someone really wants a mouse bind on 0 or 1, they can edit their binds file
        if (listening) {
            if (button > GLFW_MOUSE_BUTTON_2) {
                listening = false
                setting.value.type = DeviceType.MOUSE
                setting.value.code = button
            } else if (button == GLFW_MOUSE_BUTTON_2) {
                listening = false
                setting.value.code = GLFW_KEY_UNKNOWN
            }
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

    companion object {
        private val SETTING_BG = Color(19, 19, 19)
    }
}