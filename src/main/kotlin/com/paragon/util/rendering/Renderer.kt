package com.paragon.util.rendering

import com.mojang.blaze3d.systems.RenderSystem
import com.paragon.backend.setting.Colour
import com.paragon.util.mc
import net.minecraft.client.render.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import java.util.function.Consumer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


/**
 * @author surge
 * @since 12/02/2023
 */
object Renderer {

    private val actions: MutableList<Action> = ArrayList()

    fun submit(action: Action): Renderer {
        actions.add(action)

        return this
    }

    fun submit(action: () -> Unit): Renderer {
        actions.add(object : Action() {
            override fun draw() {
                action()
            }
        })

        return this
    }

    fun optional(submit: Boolean, action: Action): Renderer {
        if (submit) {
            submit(action)
        }

        return this
    }

    @JvmStatic
    fun drawAndClear() {
        actions.forEach(Consumer { obj: Action -> obj.draw() })
        actions.clear()
    }

    fun line(matrices: MatrixStack, start: Vec3d, end: Vec3d, lineWidth: Float, colour: Colour, secondary: Colour = colour): Action {
        return object : Action() {
            override fun draw() {
                lineImplementation(matrices, start, end, lineWidth, colour, secondary)
            }
        }
    }

    fun box(matrices: MatrixStack, bb: Box, colour: Colour, mode: DrawMode) {
        submit {
            boxImplementation(matrices, bb, colour, mode)
        }
    }

    fun polygon(matrices: MatrixStack, center: Vec3d, radius: Double, colour: Colour, mode: DrawMode, sides: Int, outsideOffset: Double = 0.0) {
        submit {
            polygonImplementation(matrices, center, radius, colour, mode, sides, outsideOffset)
        }
    }

    private fun filledBBVertices(matrix: Matrix4f, builder: BufferBuilder, box: Box, colour: Colour) {
        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
    }

    private fun outlineBBVertices(matrix: Matrix4f, builder: BufferBuilder, box: Box, colour: Colour) {
        // the most comedic rendering

        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(colour.rgb).next()

        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(colour.rgb).next()

        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.minZ.toFloat()).color(0).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(0).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(colour.rgb).next()

        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(0).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(0).next()
        builder.vertex(matrix, box.maxX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(colour.rgb).next()

        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.minX.toFloat(), box.minY.toFloat(), box.maxZ.toFloat()).color(0).next()
        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(0).next()
        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.maxZ.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, box.minX.toFloat(), box.maxY.toFloat(), box.minZ.toFloat()).color(colour.rgb).next()
    }

    private fun lineImplementation(matrices: MatrixStack, start: Vec3d, end: Vec3d, lineWidth: Float, colour: Colour, secondary: Colour) {
        val camera: Vec3d = mc.gameRenderer.camera.pos

        val vecStart: Vec3d = start.subtract(camera)
        val vecEnd: Vec3d = end.subtract(camera)

        val matrix: Matrix4f = matrices.peek().positionMatrix
        val builder: BufferBuilder = Tessellator.getInstance().buffer

        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }
        RenderSystem.lineWidth(lineWidth)
        RenderSystem.enableBlend()
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        builder.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR)

        builder.vertex(matrix, vecStart.x.toFloat(), vecStart.y.toFloat(), vecStart.z.toFloat()).color(colour.rgb).next()
        builder.vertex(matrix, vecEnd.x.toFloat(), vecEnd.y.toFloat(), vecEnd.z.toFloat()).color(secondary.rgb).next()

        BufferRenderer.drawWithGlobalProgram(builder.end())
    }

    private fun boxImplementation(matrices: MatrixStack, bb: Box, colour: Colour, mode: DrawMode) {
        val matrix: Matrix4f = matrices.peek().positionMatrix
        val builder: BufferBuilder = Tessellator.getInstance().buffer
        val cameraPos = mc.gameRenderer.camera.pos
        val box = bb.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z)

        RenderSystem.depthFunc(GL_ALWAYS)
        RenderSystem.enableBlend()
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }

        builder.begin(mode.mode, VertexFormats.POSITION_COLOR)

        mode.vertices(matrix, builder, box, colour)

        BufferRenderer.drawWithGlobalProgram(builder.end())

        RenderSystem.disableBlend()
        RenderSystem.depthFunc(GL_LEQUAL)
    }

    private fun polygonImplementation(matrices: MatrixStack, center: Vec3d, radius: Double, colour: Colour, mode: DrawMode, sides: Int, outsideOffset: Double = 0.0) {
        val matrix: Matrix4f = matrices.peek().positionMatrix
        val builder: BufferBuilder = Tessellator.getInstance().buffer
        val cameraPos = mc.gameRenderer.camera.pos
        val center = Vec3d(center.x - cameraPos.x, center.y - cameraPos.y, center.z - cameraPos.z)

        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }

        RenderSystem.disableCull()
        RenderSystem.depthFunc(GL_ALWAYS)
        RenderSystem.enableBlend()

        builder.begin(mode.mode, VertexFormats.POSITION_COLOR)

        val angleIncrement = 2.0 * PI / sides
        var angle = 0.0

        /* for (i in 0 until sides) {
            builder.vertex(matrix, (center.x + radius * cos(angle)).toFloat(), center.y.toFloat(), (center.z + radius * sin(angle)).toFloat()).color(colour.rgb).next()

            if (mode == DrawMode.FILL) {
                builder.vertex(matrix, center.x.toFloat(), center.y.toFloat(), center.z.toFloat()).color(colour.rgb).next()
                builder.vertex(matrix, (center.x + radius * cos(angle + angleIncrement)).toFloat(), center.y.toFloat(), (center.z + radius * sin(angle + angleIncrement)).toFloat()).color(colour.rgb).next()
                builder.vertex(matrix, (center.x + radius * cos(angle)).toFloat(), center.y.toFloat(), (center.z + radius * sin(angle)).toFloat()).color(colour.rgb).next()
            }

            angle += angleIncrement
        } */

        for (i in 0 until sides) {
            builder.vertex(matrix, (center.x + radius * cos(angle)).toFloat(), center.y.toFloat() + outsideOffset.toFloat(), (center.z + radius * sin(angle)).toFloat()).color(colour.rgb).next()

            if (mode == DrawMode.FILL) {
                builder.vertex(matrix, center.x.toFloat(), center.y.toFloat(), center.z.toFloat()).color(colour.rgb).next()
                builder.vertex(matrix, (center.x + radius * cos(angle + angleIncrement)).toFloat(), center.y.toFloat() + outsideOffset.toFloat(), (center.z + radius * sin(angle + angleIncrement)).toFloat()).color(colour.rgb).next()
                builder.vertex(matrix, (center.x + radius * cos(angle)).toFloat(), center.y.toFloat() + outsideOffset.toFloat(), (center.z + radius * sin(angle)).toFloat()).color(colour.rgb).next()
            }

            angle += angleIncrement
        }

        // Add final vertex to close the polygon
        builder.vertex(matrix, (center.x + radius).toFloat(), center.y.toFloat() + outsideOffset.toFloat(), center.z.toFloat()).color(colour.rgb).next()

        BufferRenderer.drawWithGlobalProgram(builder.end())

        RenderSystem.disableBlend()
        RenderSystem.depthFunc(GL_LEQUAL)
        RenderSystem.enableCull()
    }

    fun crosshair(): Vec3d {
        val camera: Camera = mc.gameRenderer.camera

        val yawRadius = Math.toRadians(-camera.yaw.toDouble()).toFloat()
        val pitchRadius = Math.toRadians(-camera.pitch.toDouble()).toFloat()
        val pitch: Float = -MathHelper.cos(pitchRadius)

        return Vec3d(
            (MathHelper.sin((yawRadius - Math.PI).toFloat()) * pitch).toDouble(),
            MathHelper.sin(pitchRadius).toDouble(),
            (MathHelper.cos((yawRadius - Math.PI).toFloat()) * pitch).toDouble()
        ).add(camera.pos)
    }

    abstract class Action {
        abstract fun draw()
    }

    enum class DrawMode(val mode: VertexFormat.DrawMode, val vertices: (Matrix4f, BufferBuilder, Box, Colour) -> Unit) {
        FILL(VertexFormat.DrawMode.QUADS, { matrix, builder, box, colour -> filledBBVertices(matrix, builder, box, colour) }),
        LINES(VertexFormat.DrawMode.DEBUG_LINE_STRIP, { matrix, builder, box, colour -> outlineBBVertices(matrix, builder, box, colour) })
    }

}