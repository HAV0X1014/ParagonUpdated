package com.paragon.client.modules.movement

import com.paragon.backend.event.events.input.control.ItemSlowdownEvent
import com.paragon.backend.event.events.input.control.SneakSlowdownEvent
import com.paragon.backend.event.events.move.MoveUpdateEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket

/**
 * @author aesthetical
 * @since 02/17/23
 */
object NoSlowDown : Module("No Slow Down", "Stops you from getting slowed down", Category.MOVEMENT) {

    private val mode by enum("Mode", Mode.VANILLA, "How to bypass the anti-cheat")
    private val sneak by bool("Sneak", false, "Stops sneaking from slowing you down")

    override val info = { mode.toString() }

    @EventListener
    fun onItemSlowdown(event: ItemSlowdownEvent) {
        if (mc.player!!.isUsingItem && !mc.player!!.isRiding && event.entity == mc.player) {
            event.isCancelled = true
        }
    }

    @EventListener
    fun onMoveUpdate(event: MoveUpdateEvent) {
        if (mc.player!!.isUsingItem && !mc.player!!.isRiding && mode == Mode.NEW_NCP) {
            mc.player!!.networkHandler.sendPacket(UpdateSelectedSlotC2SPacket(mc.player!!.inventory.selectedSlot))
        }
    }

    @EventListener
    fun onSneakSlowdown(event: SneakSlowdownEvent) {
        if (event.entity == mc.player && sneak) {
            event.isCancelled = true
        }
    }

    enum class Mode {
        VANILLA,
        NEW_NCP
    }

}