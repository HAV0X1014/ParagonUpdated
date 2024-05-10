package com.paragon.client.ui.configuration.surge.module

import com.paragon.backend.bind.Bind
import com.paragon.backend.module.Module
import com.paragon.backend.setting.RegistrySetting
import com.paragon.backend.setting.Setting
import com.paragon.client.ui.configuration.surge.setting.*
import com.paragon.client.ui.configuration.surge.setting.registry.RegistrySettingElement
import com.paragon.util.fade
import com.paragon.util.getClientColour
import com.paragon.util.rendering.NVGWrapper
import com.paragon.util.rendering.ui.Element
import me.surge.animation.Animation
import me.surge.animation.Easing
import java.awt.Color

/**
 * @author surge
 * @since 11/02/2023
 */
class ModuleElement(private val module: Module, x: Float, y: Float, width: Float, height: Float) : Element(x, y, width, height) {

    private val enabled = Animation({ 150f }, false) { Easing.LINEAR }
    private val expanded = Animation({ 150f }, false) { Easing.LINEAR }

    private val children: MutableList<SettingElement<*>> = ArrayList()

    init {
        enabled.state = module.isEnabled

        module.settingMap.values.forEach {
            when (it.value) {
                is Number -> children.add(NumberSettingElement(it as Setting<Number>, x, y, width, 32f))
                is Boolean -> children.add(BooleanSettingElement(it as Setting<Boolean>, x, y, width, height))
                is Bind -> children.add(BindSettingElement(it as Setting<Bind>, x, y, width, height))
                is Enum<*> -> children.add(EnumSettingElement(it as Setting<Enum<*>>, x, y, width, height))
            }

            if (it is RegistrySetting<*>) {
                children.add(RegistrySettingElement(it, x, y, width, height))
            }
        }
    }

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        enabled.state = module.isEnabled
        nvg.quad(x, y, width, height, if (hovered(mouseX, mouseY)) hovered else background)

        nvg.text(module.name, x + 5, y + 7, Color.GRAY.fade(getClientColour(), enabled.animationFactor), size = 13f, shadow = false)

        if (children.size > 2) {
            val ind = if (expanded.animationFactor > 0) " .." else "..."
            nvg.text(ind, x + width - (nvg.textWidth(ind) + 5), y + 6, Color.WHITE, size = 12f, shadow = true)
        }

        if (expanded.animationFactor > 0) {
            nvg.scissor(x, y + height, width, getOffset() - height) {
                var offset = y + height

                children.filter { it.setting.visibility() }.forEach { child ->
                    child.x = x + 2
                    child.y = offset

                    child.render(nvg, mouseX, mouseY, mouseDelta)
                    nvg.quad(x, offset, 2f, child.getOffset(), getClientColour())

                    offset += child.getOffset()
                }
            }
        }
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY)) {
            if (button == 0) {
                module.toggle()
                return true
            } else if (button == 1) {
                expanded.state = !expanded.state
                return true
            }
        }

        if (expanded.state) {
            if (children.filter { it.setting.visibility() }.any { it.mouseClick(mouseX, mouseY, button) }) {
                return true
            }
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

    override fun mouseRelease(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (expanded.state) {
            if (children.filter { it.setting.visibility() }.any { it.mouseRelease(mouseX, mouseY, button) }) {
                return true
            }
        }

        return super.mouseRelease(mouseX, mouseY, button)
    }

    override fun keyTyped(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (expanded.state) {
            children.filter { it.setting.visibility() }.forEach { it.keyTyped(keyCode, scanCode, modifiers) }
        }

        super.keyTyped(keyCode, scanCode, modifiers)
    }

    override fun getOffset(): Float {
        return (super.getOffset() + children.filter { it.setting.visibility() }.sumOf { it.getOffset().toDouble() }.toFloat() * expanded.animationFactor).toFloat()
    }

}