package com.paragon.client.ui.configuration.aesthetical.elements

import com.paragon.backend.module.Module
import com.paragon.util.rendering.NVGWrapper
import java.awt.Color

/**
 * @author aesthetical
 * @since 02/23/23
 */
class ModuleCategoryPanel(private val categoryName: String, modules: List<Module>) : SettingElement() {

    private val children = mutableListOf<SettingElement>()

    init {
        modules.forEach { children.add(ModuleElement(it)) }
    }

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {

        nvg.quad(x, y, width * 2, getTotalHeight() * 2, PANEL_COLOR)
        nvg.text(categoryName, x + 4.0f, y + (((height * 2) / 2.0f) - 16.0f / 2.0f), Color.WHITE, "axiforma", shadow = true)

        var posY = y + (height * 2) + (PADDING * 2)
        for (child in children) {
            if (!child.visible()) {
                continue
            }

            child.x = x + (PADDING * 2)
            child.width = (width * 2) - (PADDING * 4.0f)
            child.y = posY
            child.height = 13.5f

            child.render(nvg, mouseX, mouseY, mouseDelta)
            posY += child.getTotalHeight() * 2
        }

    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        children.forEach { it.mouseClick(mouseX, mouseY, button) }
        return super.mouseClick(mouseX, mouseY, button)
    }

    override fun mouseRelease(mouseX: Int, mouseY: Int, button: Int): Boolean {
        children.forEach { it.mouseRelease(mouseX, mouseY, button) }
        return super.mouseRelease(mouseX, mouseY, button)
    }

    override fun keyTyped(keyCode: Int, scanCode: Int, modifiers: Int) {
        children.forEach { it.keyTyped(keyCode, scanCode, modifiers) }
        super.keyTyped(keyCode, scanCode, modifiers)
    }

    override fun getTotalHeight(): Float {
        var height = height + PADDING
        for (component in children) {
            height += component.getTotalHeight()
        }
        return (height + PADDING)
    }

    companion object {
        private val PANEL_COLOR = Color(30, 30, 30)
        private const val PADDING = 1.0f
    }
}