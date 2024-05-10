package com.paragon.client.modules.movement

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.event.events.net.PacketEvent.Outbound
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.calculations.MoveUtil.moving
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket

/**
 * @author aesthetical, KassuK
 * @since 02/18/23
 */
object Sprint : Module("Sprint", "Automatically sprints for you", Category.MOVEMENT) {

    private val mode by enum("Mode", Mode.LEGIT, "How to sprint")
    private val fullRage by bool("Full Rage", true, "If to never stop sprinting server side") visibility  { mode == Mode.RAGE }

    override val info = { mode.toString() }

    @EventListener
    fun onTick(event: TickEvent) {
        if (!mc.player!!.isSprinting) {
            mc.player!!.isSprinting = when (mode) {
                Mode.LEGIT -> (mc.player!!.input.movementForward > 0.0f && !mc.player!!.isSneaking && !mc.player!!.isUsingItem && mc.player!!.hungerManager.foodLevel > 6) && !mc.player!!.horizontalCollision
                Mode.OMNI -> moving() && !mc.player!!.horizontalCollision && mc.player!!.hungerManager.foodLevel > 6
                else -> true
            }
        }
    }

    @EventListener
    fun onPacketOutbound(event: Outbound) {
        if (mode == Mode.RAGE && fullRage && event.packet is ClientCommandC2SPacket) {
            if (event.packet.mode == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                event.isCancelled = true
            }
        }
    }

    enum class Mode {
        LEGIT,
        RAGE,
        OMNI
    }

}