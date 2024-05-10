package com.paragon.client.ui.configuration.surge.panel

import com.paragon.Paragon
import com.paragon.backend.module.Category
import com.paragon.client.ui.configuration.surge.module.ModuleElement
import com.paragon.util.formatCapitalised
import com.paragon.util.getClientColour
import com.paragon.util.rendering.NVGWrapper
import com.paragon.util.rendering.ui.Element
import me.surge.animation.Animation
import me.surge.animation.Easing
import org.lwjgl.nanovg.NanoVG
import java.awt.Color

/**
 * @author surge
 * @since 11/02/2023
 */
class Panel(private val category: Category, x: Float, y: Float) : Element(x, y, 190f, 600f) {

    private val bar = 35f
    private val elements: MutableList<ModuleElement> = ArrayList()
    private val expanded = Animation({ 150f }, true) { Easing.LINEAR }
    private var dragging = false
    private var lastX = 0f
    private var lastY = 0f

    init {
        Paragon.moduleManager
            .getModules { it.category == this.category }
            .forEach { elements.add(ModuleElement(it, x, y, width, 24f)) }
    }

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        if (dragging) {
            x = mouseX - lastX
            y = mouseY - lastY
        }

        val scissorHeight = (moduleHeight * expanded.animationFactor).toFloat()
        
        nvg.quad(x, y, width, bar + scissorHeight + 4, Color(16, 16, 16))
        nvg.text(category.name.formatCapitalised(), x + width / 2, y + 9, Color.WHITE, "inter", 18f, false, NanoVG.NVG_ALIGN_CENTER or NanoVG.NVG_ALIGN_TOP)
        nvg.quad(x, y + bar - 2, width, 2f, getClientColour())

        nvg.scissor(x, y + 35, width, scissorHeight) {
            nvg.quad(x, y + 35, width, scissorHeight, Color(19, 22, 24))

            var offset = y + bar

            elements.forEach {
                it.x = x
                it.y = offset
                it.render(nvg, mouseX, mouseY, mouseDelta)
                offset += it.getOffset()
            }
        }
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY) && mouseY <= y + bar) {
            if (button == 0) {
                dragging = true
                lastX = mouseX - x
                lastY = mouseY - y
                return true
            } else if (button == 1) {
                expanded.state = !expanded.state
                return true
            }
        }

        if (elements.any { it.mouseClick(mouseX, mouseY, button) }) {
            return true
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

    override fun mouseRelease(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (dragging && button == 0) {
            dragging = false
            return true
        }

        if (elements.any { it.mouseRelease(mouseX, mouseY, button) }) {
            return true
        }

        return super.mouseRelease(mouseX, mouseY, button)
    }

    override fun keyTyped(keyCode: Int, scanCode: Int, modifiers: Int) {
        elements.forEach { it.keyTyped(keyCode, scanCode, modifiers) }
        super.keyTyped(keyCode, scanCode, modifiers)
    }

    private val moduleHeight: Float
        get() {
            var height = 0f

            for (element in elements) {
                height += element.getOffset()
            }

            return height
        }
}