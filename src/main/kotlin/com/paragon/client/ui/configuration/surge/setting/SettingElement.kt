package com.paragon.client.ui.configuration.surge.setting

import com.paragon.backend.setting.Setting
import com.paragon.util.rendering.NVGWrapper
import com.paragon.util.rendering.ui.Element

/**
 * @author surge
 * @since 11/02/2023
 */
open class SettingElement<T>(val setting: Setting<T>, x: Float, y: Float, width: Float, height: Float) : Element(x, y, width, height) {

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {}

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        return super.mouseClick(mouseX, mouseY, button)
    }

    override fun mouseRelease(mouseX: Int, mouseY: Int, button: Int): Boolean {
        return super.mouseRelease(mouseX, mouseY, button)
    }

}