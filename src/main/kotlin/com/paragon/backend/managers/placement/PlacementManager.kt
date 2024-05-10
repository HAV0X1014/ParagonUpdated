package com.paragon.backend.managers.placement

import com.paragon.Paragon
import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.util.mc
import com.paragon.util.player.PlayerUtil
import me.bush.eventbus.annotation.EventListener
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * @author surge
 * @since 09/03/2023
 */
class PlacementManager {

    private val placements = LinkedHashMap<BlockPos, PlacementData>()

    init {
        Paragon.bus.subscribe(this)
    }

    @EventListener
    fun onTick(event: TickEvent) {
        if (placements.isEmpty()) {
            return
        }

        val position = placements.keys.first()
        val placement = placements[position]!!

        Paragon.inventoryManager.swap(placement.slot)
        Paragon.inventoryManager.sync()

        val res: ActionResult = mc.interactionManager!!.interactBlock(
            mc.player,
            Hand.MAIN_HAND,

            BlockHitResult(Vec3d(position.x.toDouble(), position.y.toDouble(), position.z.toDouble()).add(0.5, 0.5, 0.5), placement.direction, position, false)
        )

        if (res.isAccepted) {
            if (res.shouldSwingHand()) {
                when (placement.swing) {
                    Swing.CLIENT -> mc.player!!.swingHand(Hand.MAIN_HAND)
                    Swing.SERVER -> PlayerUtil.silentSwing(Hand.MAIN_HAND)
                    else -> {}
                }
            }

            placement.accept(res)
        }

        if (mc.player!!.inventory.selectedSlot != placement.slot) {
            Paragon.inventoryManager.swap(placement.originalSlot)
            Paragon.inventoryManager.sync()
        }

        placements.remove(placements.keys.first())
    }

    fun submit(position: BlockPos, data: PlacementData) {
        this.placements[position] = data
    }

}