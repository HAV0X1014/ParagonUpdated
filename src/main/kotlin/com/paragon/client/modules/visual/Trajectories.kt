package com.paragon.client.modules.visual

import com.paragon.Paragon
import com.paragon.backend.event.events.render.GameRenderEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.backend.setting.Colour
import com.paragon.util.calculations.MathsUtil.interpolate
import com.paragon.util.component1
import com.paragon.util.component2
import com.paragon.util.component3
import com.paragon.util.mc
import com.paragon.util.rendering.Renderer
import me.bush.eventbus.annotation.EventListener
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileUtil
import net.minecraft.item.*
import net.minecraft.util.Hand
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext


/**
 * @author aesthetical
 * @since 02/25/23
 */
object Trajectories : Module("Trajectories", "Shows the landing path of a throwable", Category.VISUAL) {

    @EventListener
    fun onGameRender(event: GameRenderEvent) {
        if (mc.player == null) {
            return
        }

        val landingPath = calcPath(mc.player!!, event.tickDelta)
        if (landingPath != null) {
            Renderer.box(event.matrices, Box(landingPath.final, landingPath.final.add(1.0, 1.0, 1.0)), Colour(255, 255, 255, 100), Renderer.DrawMode.FILL)
        }
    }

    private fun calcPath(e: PlayerEntity, tickDelta: Float): LandingPath? {
        if (e == null || e.isDead) {
            return null
        }

        val stack = if (mc.player!!.activeHand == Hand.OFF_HAND) {
            mc.player!!.getStackInHand(Hand.OFF_HAND)
        } else Paragon.inventoryManager.serverStack

        if ((stack.item !is BowItem && stack.item !is PotionItem && stack.item !is TridentItem && stack.item !is EnderPearlItem && stack.item !is SnowballItem && stack.item !is EggItem)) {
            return null
        }

        val yaw = Paragon.rotationManager.server[0]
        val pitch = Paragon.rotationManager.server[1]

        var (x, y, z) = interpolate(e, tickDelta)

        var velocity = 0.0f
        var inaccuracy = 0.0f

        when (stack.item) {

            is EnderPearlItem, is EggItem, is SnowballItem -> velocity = 1.5f
            is ExperienceBottleItem -> {
                velocity = 0.7f
                inaccuracy = -20.0f
            }
            is PotionItem -> velocity = 0.5f
            is BowItem -> {
                velocity = BowItem.getPullProgress(stack.maxUseTime - e.itemUseTimeLeft) * 1.5f
                inaccuracy = 1.0f
            }
        }

        var motionX = (-MathHelper.sin(yaw / 180.0f * Math.PI.toFloat()) * MathHelper.cos(pitch / 180.0f * Math.PI.toFloat()) * 0.4f).toDouble()
        var motionY = (-MathHelper.sin((pitch + inaccuracy) / 180.0f * Math.PI.toFloat()) * 0.4f).toDouble()
        var motionZ = (MathHelper.cos(yaw / 180.0f * Math.PI.toFloat()) * MathHelper.cos(pitch / 180.0f * Math.PI.toFloat()) * 0.4f).toDouble()

        val distance: Double = MathHelper.square(motionX * motionX + motionY * motionY + motionZ * motionZ)

        motionX /= distance
        motionY /= distance
        motionZ /= distance

        motionX += 0.007499999832361937 * inaccuracy
        motionY += 0.007499999832361937 * inaccuracy
        motionZ += 0.007499999832361937 * inaccuracy

        motionX *= velocity.toDouble()
        motionY *= velocity.toDouble()
        motionZ *= velocity.toDouble()

        val size = if (stack.item is BowItem) 0.5 else 0.25
        val path = mutableListOf<Vec3d>()

        var finalResult: HitResult? = null

        while (y > 0.0) {
            val pos = Vec3d(x, y, z)
            val motion = Vec3d(x + motionX, y + motionY, z + motionZ)

            // TODO: raycastBlocks or find a better way (this is kinda shit)
            val result = mc.world!!.raycast(RaycastContext(pos, motion, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e))
            if (result != null && result.type.equals(HitResult.Type.BLOCK)) {
                finalResult = result
                break
            }

            val bb = Box(
                    x - size, y - size, z - size,
                    x + size, y + size, z + size)
                    .stretch(motionX, motionY, motionZ)
                    .expand(1.0, 1.0, 1.0)

            val entitiesColliding = mc.world!!.getOtherEntities(e, bb)
            if (entitiesColliding.isNotEmpty()) {
                for (entity in entitiesColliding) {
                    if (!entity.isCollidable || entity.equals(e)) {
                        continue
                    }

                    val box = entity.boundingBox.expand(0.3)
                    val r = ProjectileUtil.getEntityCollision(mc.world, entity, pos, motion, box) { it != null && !it.isSpectator && !it.equals(e) }
                    if (r != null) {
                        println("collided with $entity")
                        finalResult = r
                        break
                    }
                }
            }

            x += motionX
            y += motionY
            z += motionZ

            motionX *= 0.99
            motionY *= 0.99
            motionZ *= 0.99

            motionY -= if (stack.item is ExperienceBottleItem) {
                0.07
            } else if (stack.item is BowItem || stack.item is PotionItem) {
                0.6
            } else {
                0.03
            }

            println("pathed +(${path.size})")
            path.add(Vec3d(x, y, z))
        }

        println("final: $x, $y, $z with ${path.size} paths. final result class: ${if (finalResult == null) "null" else finalResult.javaClass.simpleName}")
        return LandingPath(Vec3d(x, y, z), path, finalResult)
    }

    data class LandingPath(val final: Vec3d, val path: List<Vec3d>, val result: HitResult?)
}
