package com.paragon.client.ui.configuration.surge.setting.registry

import com.paragon.backend.setting.RegistrySetting
import com.paragon.util.getClientColour
import com.paragon.util.mc
import com.paragon.util.offset
import com.paragon.util.rendering.NVGWrapper
import com.paragon.util.tempGrow
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper
import org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER
import org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP
import java.awt.Color
import java.awt.Rectangle
import java.lang.Double.max

/**
 * @author surge
 * @since 06/03/2023
 */
class RegistryScreen<T>(val setting: RegistrySetting<T>, val last: Screen) : Screen(Text.of("")) {

    private val elements = mutableListOf<RegistryElement<T>>()

    private var bounds = Rectangle(width / 2 - 110, height / 2 - 90, 220, 180)
    private var scroll = 0f

    init {
        setting.states.forEach { (key, _) ->
            elements.add(RegistryElement(key, setting, 0f, 0f, 200f, 24f))
        }
    }

    override fun init() {
        bounds = Rectangle(width - 414, height - 220, 826, 440)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        var totalHeight = 0.0

        // lets just ignore this
        var i = 0

        elements.forEach {
            i++

            if (i % 4 == 0) {
                totalHeight += it.height + 2f
            }
        }

        scroll = MathHelper.clamp(scroll.toDouble(), -max(
            0.0,
            totalHeight - MathHelper.clamp(totalHeight, 0.0, bounds.height - 36.0)
        ), 0.0).toFloat()

        NVGWrapper.scope { nvg ->
            nvg.quad(0f, 0f, width.toFloat() * 2, height.toFloat() * 2, Color(0, 0, 0, 150))
            nvg.quad(bounds.tempGrow(0, 30), Color(16, 16, 16))

            nvg.text(setting.name, bounds.x + bounds.width / 2f, bounds.y + 5f, size = 20f, alignment = NVG_ALIGN_CENTER or NVG_ALIGN_TOP)
            nvg.quad(bounds.offset(10, 28).tempGrow(-20, -(bounds.height) + 2), getClientColour())

            // lmao.
            var i = 0

            var offsetX = bounds.x + 10f
            var offsetY = bounds.y + 30f + scroll

            nvg.scissor(bounds.x.toFloat(), bounds.y.toFloat() + 30f, bounds.width.toFloat(), bounds.height.toFloat() - 10f) {
                elements.sortedBy { it.element.toString() }.forEach {
                    it.x = offsetX
                    it.y = offsetY

                    it.render(nvg, mc.mouse.x.toInt(), mc.mouse.y.toInt(), delta)

                    offsetX += it.width + 2f

                    i++

                    if (i % 4 == 0) {
                        offsetX = bounds.x + 10f
                        offsetY += it.height + 2f
                    }
                }
            }
        }

        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (bounds.offset(0, 30).contains(mc.mouse.x.toInt(), mc.mouse.y.toInt())) {
            elements.forEach {
                if (it.mouseClick(mc.mouse.x.toInt(), mc.mouse.y.toInt(), button)) {
                    return true
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if (bounds.offset(0, 30).contains(mc.mouse.x.toInt(), mc.mouse.y.toInt())) {
            scroll += amount.toFloat() * 18
        }

        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    override fun close() {
        client!!.setScreen(last)
    }

}