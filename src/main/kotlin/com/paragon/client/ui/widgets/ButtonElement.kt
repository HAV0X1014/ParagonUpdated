package com.paragon.client.ui.widgets

import com.paragon.util.fade
import com.paragon.util.rendering.NVGWrapper
import com.paragon.util.rendering.ui.Element
import me.surge.animation.Animation
import me.surge.animation.Easing
import org.lwjgl.nanovg.NanoVG
import java.awt.Color

/**
 * @author surge
 * @since 07/03/2023
 */
class ButtonElement(val text: String, x: Float, y: Float, width: Float, height: Float, val clicked: () -> Unit) : Element(x, y, width, height) {

    private val hover = Animation(100f, false, Easing.LINEAR)

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, delta: Float) {
        hover.state = hovered(mouseX, mouseY)

        nvg.roundedQuad(x, y, width, height, 7f, Color(23, 23, 23).fade(Color(33, 33, 33), hover.animationFactor))
        nvg.text(text, x + width / 2f, y + height / 2f + 1, Color.WHITE, "inter", 12f, false, alignment = NanoVG.NVG_ALIGN_CENTER or NanoVG.NVG_ALIGN_MIDDLE)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hover.state) {
            clicked()
            return true
        }

        return false
    }

}