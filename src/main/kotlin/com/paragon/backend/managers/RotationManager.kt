package com.paragon.backend.managers

import com.paragon.Paragon
import com.paragon.backend.event.EventEra
import com.paragon.backend.event.events.move.MoveUpdateEvent
import com.paragon.backend.event.events.net.PacketEvent.Outbound
import com.paragon.mixin.duck.ILivingEntity
import com.paragon.util.calculations.Timer
import com.paragon.util.calculations.rotation.RotationUtil
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.MathHelper


/**
 * @author aesthetical
 * @since 02/17/23
 */
class RotationManager {

    private val releaseTimer: Timer

    val client: FloatArray
    val server: FloatArray

    init {
        Paragon.bus.subscribe(this)

        client = floatArrayOf(Float.NaN, Float.NaN)
        server = floatArrayOf(0.0f, 0.0f)
        releaseTimer = Timer()
    }

    @EventListener
    fun onPacketOutbound(event: Outbound) {
        if (event.packet is PlayerMoveC2SPacket && event.packet.changesLook()) {
            server[0] = event.packet.getYaw(mc.player!!.yaw)
            server[1] = event.packet.getPitch(mc.player!!.pitch)
        }
    }

    @EventListener(recieveCancelled = true)
    fun onMoveUpdate(event: MoveUpdateEvent) {
        if (event.era == EventEra.PRE && mc.player != null) {
            (mc.player as ILivingEntity?)!!.renderRotations[0] = server[0]
            (mc.player as ILivingEntity?)!!.renderRotations[1] = server[1]
        }

        if (RotationUtil.isValid(client)) {
            if (releaseTimer.elapsed(KEEP_TIME.toDouble())) {
                client[0] = Float.NaN
                client[1] = Float.NaN
                return
            }

            event.yaw = client[0]
            event.pitch = client[1]
            event.isCancelled = true

            if (event.era == EventEra.PRE) {
                rotateBody()
            }
        }
    }

    private fun rotateBody() {
        val xDiff = mc.player!!.x - mc.player!!.prevX
        val yDiff = mc.player!!.z - mc.player!!.prevZ

        val distance = (xDiff * xDiff + yDiff * yDiff).toFloat()
        var renderYawOffset = mc.player!!.bodyYaw

        if (distance > 0.0025000002f) {
            renderYawOffset = Math.toDegrees(MathHelper.atan2(yDiff, xDiff)).toFloat() - 90.0f
        }

        if (mc.player!!.handSwingProgress > 0.0f) {
            renderYawOffset = client[0]
        }

        val renderYawOffsetDiff = MathHelper.wrapDegrees(renderYawOffset - mc.player!!.bodyYaw)
        mc.player!!.bodyYaw += renderYawOffsetDiff * 0.3f

        var rotationDiff = MathHelper.wrapDegrees(client[0] - mc.player!!.bodyYaw)

        if (rotationDiff < -75.0f) {
            rotationDiff = -75.0f
        }

        if (rotationDiff >= 75.0f) {
            rotationDiff = 75.0f
        }

        mc.player!!.bodyYaw = client[0] - rotationDiff

        if (rotationDiff * rotationDiff > 2500.0f) {
            mc.player!!.bodyYaw += rotationDiff * 0.2f
        }
    }

    fun submit(yaw: Float, pitch: Float) {
        releaseTimer.reset()

        client[0] = yaw
        client[1] = pitch
    }

    fun submit(f: FloatArray) {
        releaseTimer.reset()

        client[0] = f[0]
        client[1] = f[1]
    }

    companion object {
        private const val KEEP_TIME = 150L
    }

}