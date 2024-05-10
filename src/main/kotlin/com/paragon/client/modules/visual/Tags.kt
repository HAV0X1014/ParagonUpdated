package com.paragon.client.modules.visual

import com.mojang.blaze3d.systems.RenderSystem
import com.paragon.backend.event.events.render.GameRenderEvent
import com.paragon.backend.event.events.render.RenderNameplateEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.backend.setting.Colour
import com.paragon.util.calculations.MathsUtil.interpolate
import com.paragon.util.mc
import com.paragon.util.rendering.Renderer
import me.bush.eventbus.annotation.EventListener
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11
import java.lang.Double.max

/**
 * @author aesthetical
 * @since 02/19/23
 */
object Tags : Module("Tags", "Draws tags over players heads", Category.VISUAL) {

    private val scale = float("Scale", 0.25f, 0.05f, 0.1f..0.8f, "The scaling of tags based off distance")

    @EventListener
    fun onGameRender(event: GameRenderEvent) {
        if (mc.cameraEntity != null) {
            for (entity in mc.world!!.players) {
                if (entity == null || entity == mc.player || entity.isDead) {
                    continue
                }

                val camera: Vec3d = mc.gameRenderer.camera.pos
                val pos = interpolate(entity, event.tickDelta).subtract(camera)
                Renderer.submit { render(event.matrices, entity, pos) }
            }
        }
    }

    @EventListener
    fun onRenderNameplate(event: RenderNameplateEvent) {
        if (event.entity != null && !event.entity.isDead) {
            event.isCancelled = true
        }
    }

    private fun render(matrices: MatrixStack, player: PlayerEntity, pos: Vec3d) {
        matrices.push()

        RenderSystem.enablePolygonOffset()
        RenderSystem.polygonOffset(1.0f, -1500000.0f)
        RenderSystem.disableDepthTest()

        matrices.translate(pos.x, pos.y + player.height + 0.12, pos.z)
        matrices.multiply(mc.entityRenderDispatcher.rotation)

        val distance = mc.cameraEntity!!.distanceTo(player).toDouble()
        val scaling = scale.value * max(distance, 4.0).toFloat() / 50.0f

        matrices.scale(-scaling, -scaling, scaling)

        val content = getPlayerInfo(player)
        val width: Int = mc.textRenderer.getWidth(content) / 2

        rectangle(
            matrices,
            (-width - 2).toDouble(),
            -(mc.textRenderer.fontHeight + 1).toDouble(),
            width * 2.0 + 4.0,
            mc.textRenderer.fontHeight + 3.0,
            Colour(0, 0, 0, 120)
        )

        mc.textRenderer.draw(matrices, content, -width.toFloat(), -(mc.textRenderer.fontHeight - 1).toFloat(), -1)

        var itemRenderX = -24 / 2 * player.inventory.armor.size + 16

        if (!player.getStackInHand(Hand.OFF_HAND).isEmpty) {
            renderItem(matrices, player.getStackInHand(Hand.OFF_HAND), itemRenderX)
            itemRenderX += 12
        }

        for (i in 3 downTo 0) {
            val stack = player.inventory.getArmorStack(i)

            if (!stack.isEmpty) {
                renderItem(matrices, stack, itemRenderX)
                itemRenderX += 12
            }
        }

        if (!player.getStackInHand(Hand.MAIN_HAND).isEmpty) {
            renderItem(matrices, player.getStackInHand(Hand.MAIN_HAND), itemRenderX)
        }

        RenderSystem.enableDepthTest()
        RenderSystem.polygonOffset(1.0f, 1500000.0f)
        RenderSystem.disablePolygonOffset()

        matrices.pop()
    }

    private fun renderItem(matrices: MatrixStack, stack: ItemStack, x: Int) {
        matrices.push()
        matrices.translate(x.toFloat(), -18f, 0f)
        matrices.scale(-12f, -12f, 0f)

        RenderSystem.enableDepthTest()
        RenderSystem.enableBlend()
        DiffuseLighting.disableGuiDepthLighting()

        val z: Float = mc.itemRenderer.zOffset
        mc.itemRenderer.zOffset = -150f
        mc.itemRenderer.renderItem(
            stack,
            ModelTransformation.Mode.GUI,
            0xF000F0,
            OverlayTexture.DEFAULT_UV,
            matrices,
            mc.bufferBuilders.entityVertexConsumers,
            0
        )

        mc.bufferBuilders.entityVertexConsumers.draw()
        mc.itemRenderer.zOffset = z

        DiffuseLighting.enableGuiDepthLighting()
        RenderSystem.disableBlend()
        RenderSystem.disableDepthTest()

        matrices.pop()
    }

    private fun rectangle(matrices: MatrixStack, x: Double, y: Double, w: Double, h: Double, colour: Colour) {
        val matrix = matrices.peek().positionMatrix
        val builder = Tessellator.getInstance().buffer

        GL11.glDepthFunc(GL11.GL_ALWAYS)
        GL11.glEnable(GL11.GL_BLEND)

        RenderSystem.enableBlend()
        RenderSystem.blendFuncSeparate(770, 771, 1, 0)
        RenderSystem.setShader { GameRenderer.getPositionColorProgram() }

        builder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR)

        builder.vertex(matrix, x.toFloat(), y.toFloat(), 0.0f).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, x.toFloat(), (y + h).toFloat(), 0.0f).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, (x + w).toFloat(), (y + h).toFloat(), 0.0f).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()
        builder.vertex(matrix, (x + w).toFloat(), y.toFloat(), 0.0f).color(colour.red / 255f, colour.green / 255f, colour.blue / 255f, colour.alpha / 255f).next()

        BufferRenderer.drawWithGlobalProgram(builder.end())
        RenderSystem.disableBlend()

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDepthFunc(GL11.GL_LEQUAL)
    }

    private fun getPlayerInfo(entity: PlayerEntity): String {
        val builder = StringBuilder()
        builder.append(entity.gameProfile.name)
        builder.append(" ")

        val playerHealth = entity.health + entity.absorptionAmount
        var formatting = Formatting.DARK_RED

        if (playerHealth > 18.0f) {
            formatting = Formatting.GREEN
        } else if (playerHealth > 16.0f) {
            formatting = Formatting.DARK_GREEN
        } else if (playerHealth > 12.0f) {
            formatting = Formatting.YELLOW
        } else if (playerHealth > 8.0f) {
            formatting = Formatting.GOLD
        } else if (playerHealth > 5.0f) {
            formatting = Formatting.RED
        }

        builder.append(formatting)
        builder.append(String.format("%.1f", entity.health + entity.absorptionAmount))

        return builder.toString()
    }

}