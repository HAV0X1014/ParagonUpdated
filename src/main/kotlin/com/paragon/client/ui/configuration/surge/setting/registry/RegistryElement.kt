package com.paragon.client.ui.configuration.surge.setting.registry

import com.paragon.backend.setting.RegistrySetting
import com.paragon.util.fade
import com.paragon.util.formatCapitalised
import com.paragon.util.getClientColour
import com.paragon.util.rendering.NVGWrapper
import com.paragon.util.rendering.ui.Element
import me.surge.animation.Animation
import me.surge.animation.Easing
import java.awt.Color

/**
 * @author surge
 * @since 06/03/2023
 */
class RegistryElement<T>(val element: T, val setting: RegistrySetting<T>, x: Float, y: Float, width: Float, height: Float) : Element(x, y, width, height) {

    private val enabled = Animation(200f, element in setting.enabled(), Easing.LINEAR)

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        enabled.state = setting.getStateT(element)

        nvg.quad(x, y, width, height, if (hovered(mouseX, mouseY)) hovered else background)
        nvg.text(element.toString().split('.').last().formatCapitalised(), x + 5, y + 7, Color.GRAY.fade(getClientColour(), enabled.animationFactor), size = 13f, shadow = false)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY)) {
            setting.setStateT(element, !setting.getStateT(element))
            return true
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

}