package com.paragon.backend.managers

import com.paragon.Paragon
import com.paragon.backend.event.events.net.PacketEvent.Inbound
import com.paragon.backend.event.events.net.PacketEvent.Outbound
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket

/**
 * @author aesthetical
 * @since 02/18/23
 */
class InventoryManager {

    /**
     * The server-side slot. This allows us to keep track of silent swaps
     */
    var serverSlot = -1
        private set

    /**
     * If the player's inventory is opened
     */
    private val openedInventory = false

    init {
        Paragon.bus.subscribe(this)
    }

    @EventListener
    fun onPacketOutbound(event: Outbound) {
        if (event.packet is UpdateSelectedSlotC2SPacket) {
            serverSlot = event.packet.selectedSlot
        }
    }

    @EventListener
    fun onPacketInbound(event: Inbound) {
        // if for whatever reason the server decides to switch ur slot, here we go
        if (event.packet is UpdateSelectedSlotS2CPacket) {
            serverSlot = event.packet.slot
        }
    }

    /**
     * Swaps to this slot server-side
     * @param slot the slot to swap to
     */
    fun swap(slot: Int) {
        if (serverSlot != slot) {
            mc.player!!.networkHandler.sendPacket(UpdateSelectedSlotC2SPacket(slot))
        }
    }

    /**
     * Syncs the server slot with the client slot
     */
    fun sync() {
        if (serverSlot != mc.player!!.inventory.selectedSlot) {
            mc.player!!.networkHandler.sendPacket(UpdateSelectedSlotC2SPacket(mc.player!!.inventory.selectedSlot))
        }
    }

    /**
     * Checks if the [serverSlot] is not equal to the client slot
     */
    val isDesynced: Boolean
        get() {
            return serverSlot != mc.player!!.inventory.selectedSlot
        }

    val serverStack: ItemStack
        get() {
            if (serverSlot == -1) {
                serverSlot = mc.player!!.inventory.selectedSlot
            }

            return mc.player!!.inventory.main[serverSlot]
        }

    val slot: Int
        get() {
            return if (serverSlot == -1) mc.player!!.inventory.selectedSlot else serverSlot
        }

}