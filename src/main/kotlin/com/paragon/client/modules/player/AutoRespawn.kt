package com.paragon.client.modules.player

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket

/**
 * @author aesthetical
 * @since 02/18/23
 */
object AutoRespawn : Module("Auto Respawn", "Automatically respawns you", Category.PLAYER) {

    @EventListener
    fun onTick(event: TickEvent?) {
        if (mc.player!!.isDead || mc.player!!.health <= 0.0f) {
            mc.player!!.networkHandler.sendPacket(ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN))
        }
    }

}