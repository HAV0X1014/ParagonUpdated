package com.paragon.client.ui.configuration.surge

import com.paragon.Paragon
import com.paragon.backend.module.Category
import com.paragon.client.ui.configuration.surge.panel.Panel
import com.paragon.mixin.duck.IMouse
import com.paragon.util.mc
import com.paragon.util.rendering.NVGWrapper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import java.awt.Color

/**
 * @author surge
 * @since 11/02/2023
 */
class SurgeUI : Screen(Text.of("")) {

    private val panels: MutableList<Panel> = ArrayList()

    init {
        var x = 20f

        for (category in Category.values()) {
            panels.add(Panel(category, x, 20f))
            x += 200f
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        NVGWrapper.scope { nvg ->
            nvg.quad(0f, 0f, width.toFloat() * 2, height.toFloat() * 2, Color(0, 0, 0, 150))

            panels.forEach { panel: Panel ->
                panel.render(
                    nvg,
                    mc.mouse.x.toInt(),
                    mc.mouse.y.toInt(),
                    0f
                )
            }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        panels.forEach {
            if (it.mouseClick(mc.mouse.x.toInt(), mc.mouse.y.toInt(), button)) {
                return true
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        panels.forEach {
            if (it.mouseRelease(mc.mouse.x.toInt(), mc.mouse.y.toInt(), button)) {
                return true
            }
        }

        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        panels.forEach {
            if (it.scroll(mc.mouse.x.toInt(), mc.mouse.y.toInt(), amount)) {
                return true
            }
        }

        return super.mouseScrolled(mouseX, mouseY, amount)
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