package com.paragon.client.ui.configuration.aesthetical.elements

import com.paragon.backend.bind.Bind
import com.paragon.backend.module.Module
import com.paragon.backend.setting.Setting
import com.paragon.client.ui.configuration.aesthetical.elements.setting.BindSettingElement
import com.paragon.client.ui.configuration.aesthetical.elements.setting.BooleanSettingElement
import com.paragon.client.ui.configuration.aesthetical.elements.setting.EnumSettingElement
import com.paragon.client.ui.configuration.aesthetical.elements.setting.NumberSettingElement
import com.paragon.util.getClientColour
import com.paragon.util.rendering.NVGWrapper
import me.surge.animation.BoundedAnimation
import me.surge.animation.Easing
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2
import java.awt.Color

/**
 * @author aesthetical
 * @since 02/23/23
 */
class ModuleElement(private val module: Module) : SettingElement() {

    private val hoverAnimation = BoundedAnimation(0.0f, 2.0f, 20.0f, false, Easing.LINEAR)
    private val openAnimation = BoundedAnimation(0.0f, 2.0f, 200.0f, false, Easing.LINEAR)

    private val children = mutableListOf<SettingElement>()
    private var opened = false

    init {
        module.settingMap.forEach { (_, v) ->
            when (v.value) {
                is Bind -> children.add(BindSettingElement(v as Setting<Bind>))
                is Boolean -> children.add(BooleanSettingElement(v as Setting<Boolean>))
                is Enum<*> -> children.add(EnumSettingElement(v as Setting<Enum<*>>))
                is Number -> children.add(NumberSettingElement(v as Setting<Number>))
            }
        }
    }

    override fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, mouseDelta: Float) {
        hoverAnimation.state = hovered(mouseX, mouseY, h = height * 2)
        openAnimation.maximum = getComponentHeight() //- (if (openAnimation.animationValue > 0.0) height else 0.0f)

        nvg.quad(x, y, width, height * 2, if (module.isEnabled) getClientColour() else UNTOGGLED_BG)
        nvg.text(module.name, x + 4.0f + (hoverAnimation.animationFactor.toFloat() * 2.0f), y + (((height * 2) / 2.0f) - 16.0f / 2.0f), Color.WHITE, "axiforma", shadow = true)

        openAnimation.state = opened

        if (openAnimation.animationValue > 0.0) {
            var posY = y + (height * 2) + PADDING

            nvg.scissor(x, y, width, (getTotalHeight()) * 2) {
                for (child in children) {
                    if (!child.visible()) {
                        continue
                    }

                    child.x = x + PADDING
                    child.width = width - (PADDING * 2.0f)
                    child.y = posY
                    child.height = 13.5f

                    child.render(nvg, mouseX, mouseY, mouseDelta)

                    posY += child.getTotalHeight() * 2
                }
            }
        }
    }

    override fun mouseClick(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (hovered(mouseX, mouseY, h = height * 2)) {
            if (button == GLFW_MOUSE_BUTTON_1) {
                module.toggle()
            } else if (button == GLFW_MOUSE_BUTTON_2) {
                opened = !opened
            }
        }

        if (opened) {
            children.forEach { it.mouseClick(mouseX, mouseY, button) }
        }

        return super.mouseClick(mouseX, mouseY, button)
    }

    override fun mouseRelease(mouseX: Int, mouseY: Int, button: Int): Boolean {
        if (opened) {
            children.forEach { it.mouseRelease(mouseX, mouseY, button) }
        }

        return super.mouseRelease(mouseX, mouseY, button)
    }

    override fun keyTyped(keyCode: Int, scanCode: Int, modifiers: Int) {
        if (opened) {
            children.forEach { it.keyTyped(keyCode, scanCode, modifiers) }
        }

        super.keyTyped(keyCode, scanCode, modifiers)
    }

    override fun getTotalHeight(): Float {
        return if (openAnimation.animationValue > 0.0) (getComponentHeight()) else height
    }

    private fun getComponentHeight(): Float {
        var h = height

        if (openAnimation.animationValue > 0.0) {
            var additional = 0.0

            for (component in children) {
                if (component.visible()) {
                    additional += component.getTotalHeight()
                }
            }

            additional += 2.0f

            h += (additional * openAnimation.animationFactor).toFloat()
        }

        return h
    }

    companion object {
        private val PADDING = 1.5f
        private val UNTOGGLED_BG = Color(35, 35, 35)
    }

}