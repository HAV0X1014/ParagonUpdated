package com.paragon.client.ui.configuration.aesthetical

import com.paragon.Paragon
import com.paragon.backend.module.Category
import com.paragon.client.ui.configuration.aesthetical.elements.ModuleCategoryPanel
import com.paragon.util.mc
import com.paragon.util.rendering.NVGWrapper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

/**
 * @author aesthetical
 * @since 02/23/23
 */
class AestheticalUI : Screen(Text.of("ballsack")) {

    private val panels = mutableListOf<ModuleCategoryPanel>()

    init {
        var x = 4.0f
        for (category in Category.values()) {
            val moduleList = Paragon.moduleManager.getModules { it.category == category }
            if (moduleList.isNotEmpty()) {
                val panel = ModuleCategoryPanel(category.displayName, moduleList)
                panel.x = x
                panel.y = 14.0f
                panel.width = 120.0f
                panel.height = 16.5f

                x += (panel.width * 2) + 6.0f
                panels += panel
            }
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        NVGWrapper.scope { nvg ->
            //nvg.quad(0f, 0f, width.toFloat() * 2, height.toFloat() * 2, Color(0, 0, 0, 150))

            panels.forEach {
                it.render(
                        nvg,
                        mc.mouse.x.toInt(),
                        mc.mouse.y.toInt(),
                        0f
                )
            }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        panels.forEach { it.mouseClick(mc.mouse.x.toInt(), mc.mouse.y.toInt(), button) }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        panels.forEach { it.mouseRelease(mc.mouse.x.toInt(), mc.mouse.y.toInt(), button) }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        panels.forEach { it.keyTyped(keyCode, scanCode, modifiers) }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun shouldPause(): Boolean {
        return false
    }

    override fun close() {
        Paragon.moduleManager.save("current")
        super.close()
    }
}