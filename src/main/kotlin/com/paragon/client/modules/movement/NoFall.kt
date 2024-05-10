package com.paragon.client.modules.movement

import com.paragon.backend.event.events.net.PacketEvent.Outbound
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.mixin.mixins.net.packet.c2s.IPlayerMoveC2SPacket
import com.paragon.util.mc
import com.paragon.util.nullCheck
import me.bush.eventbus.annotation.EventListener
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

/**
 * @author KassuK, aesthetical
 * @since 02/18/23
 */
object NoFall : Module("No Fall", "Prevents fall damage", Category.MOVEMENT) {

    private val mode by enum("Mode", Mode.SPOOF, "The anti-cheat bypass mode")
    private val distance by float("Fall Distance", 3.0f, 0.1f,3.0f..120.0f, "The distance required to have fallen before reducing more fall damage")

    override val info = { mode.toString() }

    @EventListener
    fun onPacketOutbound(event: Outbound) {
        if (nullCheck()) {
            return
        }

        if (event.packet is PlayerMoveC2SPacket && mc.player!!.fallDistance >= distance) {
            (event.packet as IPlayerMoveC2SPacket).setOnGround(true)

            if (mode == Mode.REDUCE) {
                mc.player!!.fallDistance = 0.0f
            }
        }
    }

    enum class Mode {
        SPOOF,
        REDUCE
    }

}