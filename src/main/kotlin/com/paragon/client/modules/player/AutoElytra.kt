package com.paragon.client.modules.player

import com.paragon.Paragon
import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket

/**
 * @author surge
 * @since 28/02/2023
 */
object AutoElytra : Module("Auto Elytra", "Automatically starts gliding if you are falling", Category.PLAYER) {

    /**
     * TODO: Auto equip Elytra if not already equipped
     */

    private val distance by double("Distance", 5.0, 0.5, 1.0..50.0, "The fall distance to start gliding at")

    private var warned = false

    override val info = {
        if (mc.player!!.isOnGround || mc.player!!.isFallFlying || mc.player!!.fallDistance > distance) {
            null
        } else {
            String.format("%.2f", (distance - mc.player!!.fallDistance).toFloat())
        }
    }

    @EventListener
    fun onTick(event: TickEvent) {
        if (mc.player!!.fallDistance >= distance && !mc.player!!.isFallFlying) {
            if (mc.player!!.armorItems.any { it.item == Items.ELYTRA && it.damage != it.maxDamage }) {
                mc.player!!.networkHandler.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
                mc.player!!.startFallFlying()

                Paragon.toastManager.info(this.name, "Deploying Elytra!", 2000L)
            } else if (!warned) {
                Paragon.toastManager.warn(this.name, "Elytra not found!", 2000L)
                warned = true
            }
        }

        if (mc.player!!.isOnGround) {
            warned = false
        }
    }

}