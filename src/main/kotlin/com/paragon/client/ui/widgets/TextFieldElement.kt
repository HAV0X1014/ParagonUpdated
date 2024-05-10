package com.paragon.client.ui.widgets

import com.paragon.util.fade
import com.paragon.util.rendering.NVGWrapper
import com.paragon.util.rendering.ui.Element
import me.surge.animation.Animation
import me.surge.animation.Easing
import net.minecraft.SharedConstants
import org.lwjgl.glfw.GLFW
import java.awt.Color

/**
* @author surge
* @since 07/03/2023
*/
class TextFieldElement(val initial: String, x: Float, y: Float, width: Float, height: Float) : Element(x, y, width, height) {

    private val hover = Animation(100f, false, Easing.LINEAR)
    private val listening = Animation(100f, false, Easing.LINEAR)

    var inputted: String = ""

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, delta: Float) {
        hover.state = hovered(mouseX, mouseY)

        nvg.roundedQuad(x, y, width, height, 7f, Color(23, 23, 23).fade(Color(33, 33, 33), hover.animationFactor).fade(Color(43, 43, 43), listening.animationFactor))
        nvg.text(inputted.ifEmpty { initial }, x + 5, y + 8, Color.WHITE, "inter", 12f, false)
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hover.state) {
            listening.state = !listening.state
            return true
        }

        return false
    }

    override fun keyTyped(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (listening.state) {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                listening.state = false
                return
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (inputted.isNotEmpty()) {
                    inputted = inputted.substring(0, inputted.length - 1)
                }
            }
        }
    }

    override fun charTyped(char: Char, modifiers: Int) {
        if (listening.state && SharedConstants.isValidChar(char)) {
            inputted += char.toString()
        }
    }

}