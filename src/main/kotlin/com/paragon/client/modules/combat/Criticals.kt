package com.paragon.client.modules.combat

import com.paragon.backend.event.events.net.PacketEvent.Outbound
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.mixin.duck.IPlayerInteractEntityC2SPacket
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.entity.LivingEntity
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround
import net.minecraft.util.math.Vec3d

/**
 * @author aesthetical
 * @since 02/17/22
 */
object Criticals : Module("Criticals", "does funny extra damage", Category.COMBAT) {

    private val mode by enum("Mode", Mode.PACKET, "The anti-cheat bypass mode")
    private val stopSprint by bool("Keep Sprint", true, "Allows you to crit even when sprinting")

    override val info = { mode.toString() }

    @EventListener
    fun onPacket(event: Outbound) {
        if (event.packet is IPlayerInteractEntityC2SPacket) {
            if (event.packet.type == PlayerInteractEntityC2SPacket.InteractType.ATTACK && event.packet.entity is LivingEntity) {
                if (!mc.player!!.isOnGround || mc.player!!.isHoldingOntoLadder || mc.player!!.isSubmergedInWater || mc.player!!.isInLava) {
                    return
                }

                val sprint: Boolean = mc.player!!.isSprinting

                if (stopSprint && sprint) {
                    mc.player!!.networkHandler.sendPacket(
                        ClientCommandC2SPacket(
                            mc.player,
                            ClientCommandC2SPacket.Mode.STOP_SPRINTING
                        )
                    )
                }

                when (mode) {

                    Mode.PACKET -> {
                        mc.player!!.networkHandler.sendPacket(
                            PositionAndOnGround(
                                mc.player!!.x,
                                mc.player!!.y + 0.0625,
                                mc.player!!.z,
                                false
                            )
                        )

                        mc.player!!.networkHandler.sendPacket(
                            PositionAndOnGround(
                                mc.player!!.x,
                                mc.player!!.y,
                                mc.player!!.z,
                                false
                            )
                        )
                    }

                    Mode.NEW_NCP -> {
                        // TODO: Find NCP Updated bypasses
                    }

                    Mode.MOTION -> {
                        val vel: Vec3d = mc.player!!.getVelocity()
                        mc.player!!.setVelocity(vel.x, vel.y + 0.2, vel.z)
                    }

                }

                if (stopSprint && sprint) {
                    mc.player!!.networkHandler.sendPacket(
                        ClientCommandC2SPacket(
                            mc.player,
                            ClientCommandC2SPacket.Mode.START_SPRINTING
                        )
                    )
                }
            }
        }
    }

    enum class Mode {
        PACKET,
        NEW_NCP,
        MOTION
    }

}