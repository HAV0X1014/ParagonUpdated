package com.paragon.client.ui.configuration.surge.setting

import com.paragon.backend.setting.Setting
import com.paragon.util.getClientColour
import com.paragon.util.rendering.NVGWrapper
import java.awt.Color

/**
 * @author surge
 * @since 11/02/2023
 */
class BooleanSettingElement(setting: Setting<Boolean>, x: Float, y: Float, width: Float, height: Float) : SettingElement<Boolean>(setting, x, y, width, height) {

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        nvg.quad(x, y, width, height, if (hovered(mouseX, mouseY)) hovered else background)

        nvg.text(setting.name, x + 5, y + 7.5f, if (setting.value) getClientColour() else Color.WHITE, size = 12f, shadow = false)

        super.render(nvg, mouseX, mouseY, mouseDelta)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY) && button == 0) {
            setting.setValue(!setting.value)
            return true
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

}