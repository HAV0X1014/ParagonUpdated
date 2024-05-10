package com.paragon.client.ui.title

import com.paragon.Paragon.Companion.altManager
import com.paragon.Paragon.Companion.version
import com.paragon.backend.alt.Alt
import com.paragon.client.ui.widgets.ButtonElement
import com.paragon.client.ui.widgets.ElementContainer
import com.paragon.client.ui.widgets.TextFieldElement
import com.paragon.util.BuildConfig.BuildConfig
import com.paragon.util.fade
import com.paragon.util.mc
import com.paragon.util.rendering.NVGWrapper
import com.paragon.util.rendering.NVGWrapper.scope
import me.surge.animation.Animation
import me.surge.animation.Easing
import net.minecraft.SharedConstants
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.MathHelper
import org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE
import org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER
import org.lwjgl.nanovg.NanoVG.*
import java.awt.Color
import java.lang.Double.max

/**
 * @author surge
 * @since 22/02/2023
 */
object MainMenuHook {

    var height = 0f
    private var altManagerUI: AltManagerUI = AltManagerUI()

    fun init() {
        altManagerUI = AltManagerUI()
    }

    fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        scope { nvg ->
            nvg.text("Running Paragon v" + version + " on git " + BuildConfig.BRANCH + "/" + BuildConfig.HASH, 5f, 5f, Color.WHITE, shadow = true)
            altManagerUI.render(nvg, mouseX, mouseY, delta)
        }
    }

    fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        altManagerUI.mouseClicked(mouseX, mouseY, button)
        return false
    }

    fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        altManagerUI.keyTyped(keyCode, scanCode, modifiers)
        return false
    }

    fun mouseScrolled(mouseX: Int, mouseY: Int, amount: Float): Boolean {
        altManagerUI.mouseScrolled(mouseX, mouseY, amount)
        return false
    }

    fun charTyped(char: Char, modifiers: Int) {
        altManagerUI.charTyped(char, modifiers)
    }

    private class AltManagerUI : ElementContainer() {

        private val expand = Animation(300f, false, Easing.CIRC_IN_OUT)

        private val WIDTH = 300f

        private val x: Float
            get() {
                return (-WIDTH + ((WIDTH + 10) * expand.animationFactor)).toFloat()
            }

        private val height: Float
            get() {
                return mc.window.height - 60f
            }

        private val altEntries = mutableListOf<AltEntry>()

        private var scroll = 0f

        init {
            altManager.alts.forEach {
                altEntries.add(AltEntry(it, 0f, 0f, WIDTH - 20f, 30f))
            }

            put("email", TextFieldElement("Email", 0f, 0f, WIDTH - 20f, 25f))
            put("password", TextFieldElement("Password", 0f, 0f, WIDTH - 20f, 25f))

            put("add", ButtonElement("Add", 0f, 0f, WIDTH - 20f, 25f) {
                altManager.alts.add(Alt(get("email", TextFieldElement::class.java)!!.inputted, get("password", TextFieldElement::class.java)!!.inputted))

                get("email", TextFieldElement::class.java)!!.inputted = ""
                get("password", TextFieldElement::class.java)!!.inputted = ""
            })
        }

        fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, delta: Float) {
            altEntries.removeIf { entry -> !altManager.alts.any { alt -> entry.alt.email == alt.email } }

            altManager.alts.forEach { alt ->
                if (!altEntries.any { it.alt == alt }) {
                    altEntries.add(AltEntry(alt, 0f, 0f, WIDTH - 20f, 25f))
                }
            }

            val totalHeight = altEntries.sumOf { it.height.toDouble() + 10f }

            nvg.roundedQuad(x + WIDTH - 10f, 30f + (height / 2f) - 16, 36f, 32f, 7f, Color(16, 16, 16))
            nvg.roundedQuad(x, 30f, WIDTH, height, 10f, Color(16, 16, 16))

            nvg.text("Alt Manager", x + 10f, 39f, Color.WHITE, shadow = false)
            nvg.text(altManager.status, x + WIDTH - nvg.textWidth(altManager.status, size = 12f) - 5, 44f, Color.WHITE, "inter", 12f, false)
            nvg.quad(x + 5f, 65f, WIDTH - 10f, 1f, Color(55, 55, 55, 50))

            nvg.text(if (expand.state) "<" else ">", x + WIDTH + 12f, 30f + (height / 2f) + 9, Color.WHITE, "inter", 30f, false, alignment = NVG_ALIGN_CENTER)

            scroll = MathHelper.clamp(scroll.toDouble(), -max(0.0, totalHeight - MathHelper.clamp(altEntries.sumOf { it.height.toDouble() + 10f }, 0.0, height - 60.0)), 0.0).toFloat()

            var offset = 70f + scroll

            nvg.scissor(x + 10f, offset, WIDTH - 10f, height - 20f) {
                altEntries.forEach {
                    it.x = x + 10f
                    it.y = offset
                    it.width = WIDTH - 85f

                    it.render(nvg, mouseX, mouseY, delta)

                    offset += it.height + 10f
                }
            }

            positionAscending(x + 10f, 30f + height, 5f)
            renderElements(nvg, mouseX, mouseY, delta)
        }

        fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
            if (mouseX >= x + WIDTH && mouseX <= x + WIDTH + 30f && mouseY >= 30f + (height / 2f) - 13 && mouseY <= 30f + (height / 2f) + 13) {
                this.expand.state = !this.expand.state
                return
            }

            altEntries.forEach {
                it.clicked(button)
            }

            clickElements(mouseX, mouseY, button)
        }

        fun mouseScrolled(mouseX: Int, mouseY: Int, amount: Float) {
            if (mouseX >= x && mouseX <= x + WIDTH && mouseY >= 65f && mouseY <= 30f + height) {
                scroll += amount * 13
            }
        }

        fun keyTyped(keyCode: Int, scanCode: Int, modifiers: Int) {
            typeElements(keyCode, scanCode, modifiers)
        }

        fun charTyped(char: Char, modifiers: Int) {
            charElements(char, modifiers)
        }

    }

    private class AltEntry(val alt: Alt, var x: Float, var y: Float, var width: Float, var height: Float) : ElementContainer() {

        private val hovered = Animation(100f, false, Easing.LINEAR)

        init {
            put("delete", ButtonElement("Delete", 0f, 0f, 64f, height) {
                altManager.alts.remove(this.alt)
            })
        }

        fun render(nvg: NVGWrapper, mouseX: Int, mouseY: Int, delta: Float) {
            hovered.state = hovered(mouseX, mouseY)

            nvg.roundedQuad(x, y, width, height, 7f, Color(23, 23, 23).fade(Color(33, 33, 33), hovered.animationFactor))

            nvg.text(alt.cachedUsername, x + 5f, y + height / 2f, Color.WHITE, "inter", size = 14f, false, alignment = NVG_ALIGN_LEFT or NVG_ALIGN_MIDDLE)

            get<ButtonElement>("delete")!!
                .position(x + width + 5f, y)
                .render(nvg, mouseX, mouseY, delta)
        }

        fun clicked(button: Int) {
            if (get<ButtonElement>("delete")!!.hovered(mc.mouse.x.toInt(), mc.mouse.y.toInt())) {
                get<ButtonElement>("delete")!!.mouseClick(mc.mouse.x.toInt(), mc.mouse.y.toInt(), button)
                return
            }

            if (hovered.state && button == 0) {
                altManager.login(alt)
            }
        }

        fun hovered(mouseX: Int, mouseY: Int): Boolean {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height
        }

    }

}