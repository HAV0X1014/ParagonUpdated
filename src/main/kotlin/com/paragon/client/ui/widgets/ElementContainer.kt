package com.paragon.client.ui.widgets

import com.paragon.util.rendering.NVGWrapper
import com.paragon.util.rendering.ui.Element

/**
 * @author surge
 * @since 07/03/2023
 */
open class ElementContainer {

    val elements = hashMapOf<String, Element>()

    fun renderElements(nvg: NVGWrapper, mouseX: Int, mouseY: Int, delta: Float) {
        elements.forEach {
            it.value.render(nvg, mouseX, mouseY, delta)
        }
    }

    fun clickElements(mouseX: Int, mouseY: Int, button: Int) {
        elements.forEach {
            it.value.mouseClick(mouseX, mouseY, button)
        }
    }

    fun typeElements(keyCode: Int, scanCode: Int, modifiers: Int) {
        elements.forEach { (_, element) ->
            element.keyTyped(keyCode, scanCode, modifiers)
        }
    }

    fun charElements(char: Char, modifiers: Int) {
        elements.forEach { (_, element) ->
            element.charTyped(char, modifiers)
        }
    }

    fun positionAscending(x: Float, initialY: Float, offset: Float = 10f): ElementContainer {
        var y = initialY - offset

        elements.forEach { (_, element) ->
            y -= element.height + offset
            element.position(x, y)
        }

        return this
    }

    fun put(id: String, element: Element) {
        elements[id] = element
    }

    fun take(id: String) {
        elements.remove(id)
    }

    inline fun <reified T : Element> get(id: String, clazz: Class<T> = T::class.java): T? {
        return elements[id]?.let { clazz.cast(it) }
    }

}