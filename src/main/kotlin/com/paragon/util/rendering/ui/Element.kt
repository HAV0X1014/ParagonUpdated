package com.paragon.util.rendering.ui

import com.paragon.backend.setting.Colour
import com.paragon.util.rendering.NVGWrapper

/**
 * @author surge
 * @since 11/02/2023
 */
abstract class Element(var x: Float = 0.0f, var y: Float = 0.0f, var width: Float = 0.0f, var height: Float = 0.0f) {

    abstract fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float)

    open fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        return false
    }

    open fun mouseRelease(mouseX: Int, mouseY: Int, button: Int): Boolean {
        return false
    }

    open fun scroll(mouseX: Int, mouseY: Int, amount: Double): Boolean {
        return false
    }

    open fun keyTyped(keyCode: Int, scanCode: Int, modifiers: Int) {}
    open fun charTyped(char: Char, modifiers: Int) {}

    open fun getOffset(): Float {
        return height
    }

    fun position(x: Float, y: Float): Element {
        this.x = x
        this.y = y
        return this
    }

    fun hovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
    }

    fun hovered(mouseX: Int, mouseY: Int, w: Float = this.width, h: Float = this.height): Boolean {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h
    }

    protected val background = Colour(23, 23, 23, 255)
    protected val hovered = Colour(26, 26, 26, 255)

}