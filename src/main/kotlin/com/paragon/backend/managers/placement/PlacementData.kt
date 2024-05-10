package com.paragon.backend.managers.placement

import com.paragon.util.mc
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.Direction

/**
 * @author surge
 * @since 09/03/2023
 */
data class PlacementData(val slot: Int, val direction: Direction, val swing: Swing = Swing.CLIENT, val hand: Hand = Hand.MAIN_HAND) {

    val originalSlot = mc.player!!.inventory.selectedSlot

    var accept: (ActionResult) -> Unit = {
            if (it.shouldSwingHand()) {
                mc.player!!.swingHand(Hand.MAIN_HAND)
            }
        }

        private set

    fun accept(action: (ActionResult) -> Unit): PlacementData {
        accept = action

        return this
    }

}
