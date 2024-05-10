package com.paragon.client.ui.configuration.surge.setting

import com.paragon.backend.bind.Bind
import com.paragon.backend.bind.DeviceType
import com.paragon.backend.setting.Setting
import com.paragon.util.getClientColour
import com.paragon.util.rendering.NVGWrapper
import org.lwjgl.glfw.GLFW
import java.awt.Color

/**
 * @author surge, aesthetical
 * @since 11/02/2023
 */
class BindSettingElement(setting: Setting<Bind>, x: Float, y: Float, width: Float, height: Float) : SettingElement<Bind>(setting, x, y, width, height) {

    private var listening = false

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        nvg.quad(x, y, width, height, if (hovered(mouseX, mouseY)) hovered else background)

        if (listening) {
            nvg.quad(x, y, width, height, getClientColour())
        }

        nvg.text(setting.name, x + 5, y + 7f, Color.WHITE, size = 12f, shadow = false)
        nvg.text(setting.value.toString(), x + width - nvg.textWidth(setting.value.toString(), size = 12.0f) - 5, y + 7f, Color.GRAY, size = 12f, shadow = false)

        super.render(nvg, mouseX, mouseY, mouseDelta)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        return if (hovered(mouseX, mouseY)) {
            when (button) {
                0 -> {
                    listening = !listening
                    true
                }

                1 -> {
                    setting.value.code = GLFW.GLFW_KEY_UNKNOWN
                    listening = false
                    true
                }

                else -> {
                    setting.value.type = DeviceType.MOUSE
                    setting.value.code = button
                    listening = false
                    true
                }
            }
        } else super.mouseClick(mouseX, mouseY, button)
    }

    override fun keyTyped(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (listening) {
            setting.value.type = DeviceType.KEYBOARD
            setting.value.code = keyCode
            listening = false
        }

        super.keyTyped(keyCode, scanCode, modifiers)
    }

}