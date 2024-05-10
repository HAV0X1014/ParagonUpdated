package com.paragon.client.modules.combat

import com.paragon.backend.event.events.net.PacketEvent.Inbound
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.mixin.mixins.net.packet.s2c.IEntityVelocityUpdateS2CPacket
import com.paragon.mixin.mixins.net.packet.s2c.IExplosionS2CPacket
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket

/**
 * @author KassuK, aesthetical
 * @since 02/18/23
 */
object Velocity : Module("Velocity", "Modifies your velocity", Category.COMBAT) {

    private val cancelNull by bool("Cancel Null", true, "If to cancel velocity packets if H and V % is 0.0")
    private val horizontal by float("Horizontal", 0.0f, 1.0f, 0f..100.0f, "How much horizontal kb")
    private val vertical by float("Vertical", 0.0f, 1.0f, 0f..100.0f, "How much vertical kb")

    override val info = { "H: ${horizontal}%, V: ${vertical}%" }

    @EventListener
    fun onPacketReceive(event: Inbound) {
        if (event.packet is EntityVelocityUpdateS2CPacket && event.packet.id == mc.player!!.id) {
            if (horizontal == 0.0f && vertical == 0.0f && cancelNull) {
                event.isCancelled = true
                return
            }

            (event.packet as IEntityVelocityUpdateS2CPacket).setVelocityX((event.packet.velocityX * (horizontal / 100.0f)).toInt())
            (event.packet as IEntityVelocityUpdateS2CPacket).setVelocityY((event.packet.velocityY * (vertical / 100.0f)).toInt())
            (event.packet as IEntityVelocityUpdateS2CPacket).setVelocityZ((event.packet.velocityZ * (horizontal / 100.0f)).toInt())
        }

        if (event.packet is ExplosionS2CPacket) {
            if (horizontal == 0.0f && vertical == 0.0f && cancelNull) {
                event.isCancelled = true
                return
            }

            (event.packet as IExplosionS2CPacket).setVelocityX(event.packet.playerVelocityX * (horizontal / 100.0f))
            (event.packet as IExplosionS2CPacket).setVelocityY(event.packet.playerVelocityY * (vertical / 100.0f))
            (event.packet as IExplosionS2CPacket).setVelocityZ(event.packet.playerVelocityZ * (horizontal / 100.0f))
        }
    }

}