package com.paragon.util.rendering

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import org.lwjgl.BufferUtils
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.opengl.GL11.GL_ONE
import org.lwjgl.opengl.GL11.GL_SRC_ALPHA
import org.lwjgl.system.MemoryUtil
import java.awt.Color
import java.awt.Rectangle
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.function.Consumer

/**
 * @author surge
 * @since 11/02/2023
 */
object NVGWrapper {

    private var vg: Long = 0

    private val fontNames = arrayOf("inter", "axiforma")

    private val fonts = HashMap<String, ByteBuffer>()

    @JvmStatic
    fun initialise() {
        vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS)

        for (font in fontNames) {
            try {
                val buffer = getResourceBytes("font/$font.ttf", 1024)
                NanoVG.nvgCreateFontMem(vg, font, buffer, 0)
                fonts[font] = buffer
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    private fun beginFrame() {
        NanoVG.nvgBeginFrame(
            vg,
            MinecraftClient.getInstance().window.width.toFloat(),
            MinecraftClient.getInstance().window.height.toFloat(),
            1f
        )
    }

    private fun endFrame() {
        NanoVG.nvgEndFrame(vg)
    }

    fun quad(rectangle: Rectangle, colour: Color) {
        quad(rectangle.x.toFloat(), rectangle.y.toFloat(), rectangle.width.toFloat(), rectangle.height.toFloat(), colour)
    }

    fun quad(x: Float, y: Float, width: Float, height: Float, colour: Color) {
        val colourised = convert(colour)

        NanoVG.nvgBeginPath(vg)

        NanoVG.nvgRect(vg, x, y, width, height)
        NanoVG.nvgFillColor(vg, colourised)
        NanoVG.nvgFill(vg)

        NanoVG.nvgClosePath(vg)

        colourised.free()
    }

    fun roundedQuad(x: Float, y: Float, width: Float, height: Float, radius: Float, colour: Color) {
        roundedQuad(x, y, width, height, radius, radius, radius, radius, colour)
    }

    fun roundedQuad(x: Float, y: Float, width: Float, height: Float, tl: Float, tr: Float, bl: Float, br: Float, colour: Color) {
        val colourised = convert(colour)

        NanoVG.nvgBeginPath(vg)

        NanoVG.nvgRoundedRectVarying(vg, x, y, width, height, tl, tr, bl, br)
        NanoVG.nvgFillColor(vg, colourised)
        NanoVG.nvgFill(vg)

        NanoVG.nvgClosePath(vg)

        colourised.free()
    }

    fun text(text: String, x: Float, y: Float, colour: Color = Color.WHITE, face: String = "inter", size: Float = 16f, shadow: Boolean = false, alignment: Int = NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP) {
        if (shadow) {
            text(text, x + 1, y + 1, Color(0, 0, 0, 150), face, size, false)
        }

        val colourised = convert(colour)

        NanoVG.nvgBeginPath(vg)

        NanoVG.nvgFillColor(vg, colourised)
        NanoVG.nvgFontFace(vg, face)
        NanoVG.nvgFontSize(vg, size)
        NanoVG.nvgTextAlign(vg, alignment)
        NanoVG.nvgText(vg, x, y, text)

        NanoVG.nvgClosePath(vg)

        colourised.free()
    }

    fun textWidth(text: String, face: String = "inter", size: Float = 20f): Float {
        val bounds = FloatArray(4)

        NanoVG.nvgSave(vg)

        NanoVG.nvgFontFace(vg, face)
        NanoVG.nvgFontSize(vg, size)
        NanoVG.nvgTextBounds(vg, 0f, 0f, text, bounds)

        NanoVG.nvgRestore(vg)

        return bounds[2]
    }

    fun textHeight(face: String, size: Float): Float {
        val ascender = FloatArray(1)
        val descender = FloatArray(1)
        val height = FloatArray(1)

        NanoVG.nvgFontFace(vg, face)
        NanoVG.nvgFontSize(vg, size)
        NanoVG.nvgTextMetrics(vg, ascender, descender, height)

        return height[0]
    }

    fun scissor(x: Float, y: Float, width: Float, height: Float, block: Runnable) {
        NanoVG.nvgSave(vg)
        NanoVG.nvgIntersectScissor(vg, x, y, width, height)
        block.run()
        NanoVG.nvgRestore(vg)
    }

    fun resetScissor() {
        NanoVG.nvgResetScissor(vg)
    }

    fun terminate() {
        NanoVGGL3.nvgDelete(vg)
    }

    fun scope(block: Consumer<NVGWrapper>) {
        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE)

        beginFrame()
        block.accept(this)
        endFrame()

        RenderSystem.disableBlend()
    }

    private fun convert(colour: Color): NVGColor {
        return NVGColor.calloc()
            .r(colour.red / 255f)
            .g(colour.green / 255f)
            .b(colour.blue / 255f)
            .a(colour.alpha / 255f)
    }

    private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
        val newBuffer = BufferUtils.createByteBuffer(newCapacity)
        buffer.flip()
        newBuffer.put(buffer)
        return newBuffer
    }

    fun getResourceBytes(resource: String, bufferSize: Int): ByteBuffer {
        var buffer: ByteBuffer
        val source = javaClass.getResourceAsStream("/assets/paragon/$resource")
        val rbc = Channels.newChannel(source)

        buffer = BufferUtils.createByteBuffer(bufferSize)

        while (true) {
            val bytes = rbc.read(buffer)

            if (bytes == -1) {
                break
            }

            if (buffer.remaining() == 0) {
                buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2) // 50%
            }
        }

        buffer.flip()
        return MemoryUtil.memSlice(buffer)
    }

}