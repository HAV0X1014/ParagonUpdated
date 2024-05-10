package com.paragon.client.modules.player

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.calculations.Timer
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.slot.SlotActionType

/**
 * @author aesthetical
 * @since 02/22/23
 */
object Stealer : Module("Stealer", "Automatically steals from chests", Category.PLAYER) {
    private val delay by int("Delay", 50, 1, 0..1000, "How long to wait before stealing the next item")
    private val autoClose by bool("Auto Close", true, "If to automatically close the chest after stealing everything")

    private val timer = Timer()

    override val info = { delay.toString() }

    @EventListener
    fun onTick(event: TickEvent) {
        if (mc.player!!.currentScreenHandler is GenericContainerScreenHandler) {
            val handler = mc.player!!.currentScreenHandler as GenericContainerScreenHandler

            if (timer.elapsed(delay.toDouble())) {
                for (i in 0..(handler.rows * 9)) {
                    val stack = handler.inventory.getStack(i)
                    if (!stack.isEmpty) {
                        mc.interactionManager!!.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player)
                        timer.reset()
                        return
                    }
                }

                if (autoClose) {
                    mc.player!!.closeHandledScreen()
                }
            }
        }
    }
}