package com.paragon.util.calculations.rotation

import com.paragon.util.mc
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import java.lang.Float.isNaN
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * @author aesthetical
 * @since 02/17/23
 */
object RotationUtil {

    /**
     * Calculates rotations towards an entity
     * @param entity the entity
     * @param target the place to target the entity at
     * @return the rotations to the entity
     */
    @JvmStatic
    fun calcToEntity(entity: LivingEntity, target: Target?): FloatArray {
        val playerPos: Vec3d = mc.player!!.eyePos
        val entityPos = entity.pos

        val eyes = when (target) {
            Target.HEAD -> entityPos.add(0.0, entity.standingEyeHeight.toDouble(), 0.0)
            Target.TORSO -> entityPos.add(0.0, (entity.height * 0.75), 0.0)
            Target.LEGS -> entityPos.add(0.0, (entity.height * 0.45), 0.0)
            else -> entityPos
        }

        val deltaX = eyes.x - playerPos.x
        val deltaZ = eyes.z - playerPos.z

        val yaw = -(atan2(deltaX, deltaZ) * (180.0 / Math.PI)).toFloat()
        var pitch = (-Math.toDegrees(atan2(eyes.y - playerPos.y, hypot(deltaX, deltaZ)))).toFloat()

        if (pitch > 90.0f) {
            pitch = 90.0f
        } else if (pitch < -90.0f) {
            pitch = -90.0f
        }

        return floatArrayOf(yaw, pitch)
    }

    /**
     * Calculates angles to a block
     * @param pos the position
     * @param facing the direction of the block position
     * @return the rotations to the block at the direction
     */
    fun calcAngleToBlock(pos: BlockPos, facing: Direction): FloatArray {
        val x: Double = pos.x + 0.5 - mc.player!!.x + facing.offsetX / 2.0
        val y = pos.y + 0.5
        val z: Double = pos.z + 0.5 - mc.player!!.z + facing.offsetZ / 2.0

        val distance = sqrt(x * x + z * z)
        var yaw = (atan2(z, x) * 180.0 / Math.PI - 90.0f).toFloat()

        var pitch = (atan2(mc.player!!.y + mc.player!!.standingEyeHeight - y, distance) * 180.0 / Math.PI).toFloat()

        if (yaw < 0.0f) {
            yaw += 360.0f
        }

        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f)

        return floatArrayOf(yaw, pitch)
    }

    /**
     * Checks if a rotation provided is valid
     * @param rots the rotations
     * @return if they are valid or not
     */
    fun isValid(rots: FloatArray?): Boolean {
        if (rots == null || rots.size < 2) {
            return false
        }

        return !isNaN(rots[0]) && !isNaN(rots[1]) && abs(rots[1]) <= 90.0f
    }
}