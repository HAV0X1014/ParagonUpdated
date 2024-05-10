package com.paragon.client.ui.configuration.surge.setting.registry

import com.paragon.backend.setting.RegistrySetting
import com.paragon.client.ui.configuration.surge.setting.SettingElement
import com.paragon.util.mc
import com.paragon.util.rendering.NVGWrapper
import net.minecraft.registry.Registry
import java.awt.Color

/**
 * @author surge
 * @since 06/03/2023
 */
class RegistrySettingElement<T>(setting: RegistrySetting<T>, x: Float, y: Float, width: Float, height: Float) : SettingElement<Registry<*>>(setting, x, y, width, height) {

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        nvg.quad(x, y, width, height, if (hovered(mouseX, mouseY)) hovered else background)

        nvg.text(setting.name, x + 5, y + 7.5f, Color.WHITE, size = 12f)

        super.render(nvg, mouseX, mouseY, mouseDelta)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY) && button == 0) {
            mc.setScreen(RegistryScreen(setting as RegistrySetting<T>, mc.currentScreen!!))
            return true
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

}