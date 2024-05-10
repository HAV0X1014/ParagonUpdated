package com.paragon.client.ui.configuration.surge.setting

import com.paragon.backend.setting.Setting
import com.paragon.util.formatCapitalised
import com.paragon.util.rendering.NVGWrapper
import java.awt.Color

/**
 * @author surge
 * @since 11/02/2023
 */
class EnumSettingElement(setting: Setting<Enum<*>>, x: Float, y: Float, width: Float, height: Float) : SettingElement<Enum<*>>(setting, x, y, width, height) {

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        nvg.quad(x, y, width, height, if (hovered(mouseX, mouseY)) hovered else background)
        nvg.text(setting.name, x + 5, y + 7, Color.WHITE, size = 12f, shadow = false)

        val data = setting.value.name.formatCapitalised()

        nvg.text(data, x + width - nvg.textWidth(data, "inter", 12f) - 5, y + 7, Color.GRAY, size = 12f, shadow = false)

        super.render(nvg, mouseX, mouseY, mouseDelta)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY)) {
            if (button == 0) {
                setting.setValue(setting.nextEnum)
            } else if (button == 1) {
                setting.setValue(setting.previousEnum)
            }

            return true
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

}