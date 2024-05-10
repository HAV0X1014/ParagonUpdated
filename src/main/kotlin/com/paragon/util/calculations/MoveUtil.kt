package com.paragon.util.calculations

import com.paragon.util.mc
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.util.math.BlockPos
import kotlin.math.cos
import kotlin.math.sin


/**
 * @author aesthetical
 * @since 02/17/23
 */
object MoveUtil {

    val NULL_VELOCITY = doubleArrayOf(0.0, 0.0)

    /**
     * Checks if the local player is inputting
     * @return if they are moving/inputting
     */
    @JvmStatic
    fun moving(): Boolean {
        return mc.player!!.input.movementForward != 0.0f || mc.player!!.input.movementSideways != 0.0f
    }

    /**
     * Calculates the base NCP speed based on player conditions
     * @param potionFactor once the potion (speed/slowness) duration left is less than this, it'll stop applying the potion effect to the speed
     * @return the base speed
     */
    @JvmStatic
    fun getBaseNcpSpeed(potionFactor: Int): Double {
        var baseSpeed = 0.2783
        if (mc.player!!.hasStatusEffect(StatusEffects.SPEED)) {
            val effect = mc.player!!.getStatusEffect(StatusEffects.SPEED)
            if (effect!!.duration > potionFactor) {
                baseSpeed *= 1.0 + 0.2 * (effect.amplifier + 1)
            }
        }
        if (mc.player!!.hasStatusEffect(StatusEffects.SLOWNESS)) {
            val effect = mc.player!!.getStatusEffect(StatusEffects.SLOWNESS)
            if (effect!!.duration > potionFactor) {
                baseSpeed /= 1.0 + 0.2 * (effect.amplifier + 1)
            }
        }
        return baseSpeed
    }

    /**
     * Calculates the jump height needed based on potions
     * @param jumpHeight the base jump height
     * @return the jump height calculated
     */
    @JvmStatic
    fun getJumpHeight(jumpHeight: Double): Double {
        var jumpHeight = jumpHeight
        if (mc.player!!.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            jumpHeight += (mc.player!!.getStatusEffect(StatusEffects.JUMP_BOOST)!!.amplifier + 1.0) * 0.1
        }
        return jumpHeight
    }

    @JvmStatic
    private fun getJumpVelocityMultiplier(): Float {
        val f: Float = mc.world!!.getBlockState(mc.player!!.blockPos).block.jumpVelocityMultiplier
        val g: Float = mc.world!!.getBlockState(BlockPos(mc.player!!.pos.x, mc.player!!.boundingBox.minY - 0.5000001, mc.player!!.pos.z)).block.jumpVelocityMultiplier
        return if (f.toDouble() == 1.0) g else f
    }

    @JvmStatic
    fun getVanillaJumpVelocity(): Float {
        return 0.42f * getJumpVelocityMultiplier()
    }

    /**
     * Calculates movement strafe
     * @param moveSpeed the speed to go
     * @return the strafe x and z values
     */
    @JvmStatic
    fun strafe(moveSpeed: Double): DoubleArray {
        if (mc.player == null) {
            return NULL_VELOCITY
        }

        var forward: Float = mc.player!!.input.movementForward
        var strafe: Float = mc.player!!.input.movementSideways
        var yaw: Float = mc.player!!.yaw

        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                if (forward > 0.0f) {
                    yaw -= 45.0f
                } else {
                    yaw += 45.0f
                }
            } else if (strafe < 0.0f) {
                if (forward > 0.0f) {
                    yaw += 45.0f
                } else {
                    yaw -= 45.0f
                }
            }

            strafe = 0.0f

            if (forward > 0.0f) {
                forward = 1.0f
            } else if (forward < 0.0f) {
                forward = -1.0f
            }
        }

        val sin = -sin(Math.toRadians(yaw.toDouble()))
        val cos = cos(Math.toRadians(yaw.toDouble()))

        return doubleArrayOf(
            forward * moveSpeed * sin + strafe * moveSpeed * cos,
            forward * moveSpeed * cos - strafe * moveSpeed * sin
        )
    }
}