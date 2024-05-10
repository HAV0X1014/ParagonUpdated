package com.paragon.client.modules.player

import com.paragon.Paragon
import com.paragon.backend.event.events.input.control.AttackBlockEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.util.math.BlockPos

/**
 * @author aesthetical
 * @since 02/22/23
 */
object AutoTool : Module("Auto Tool", "Automatically swaps to the best tool", Category.PLAYER) {

    override fun enable() {
        super.enable()
        Paragon.baritoneManager.set("autoTool", false)
        Paragon.baritoneManager.set("useSwordToMine", false)
    }

    override fun disable() {
        super.disable()
        Paragon.baritoneManager.restoreValue("autoTool")
        Paragon.baritoneManager.restoreValue("useSwordToMine")
    }

    @EventListener
    fun onAttackBlock(event: AttackBlockEvent) {
        val slot = bestSlot(event.blockPos)
        if (slot == -1 || slot == Paragon.inventoryManager.slot) {
            return
        }

        mc.player!!.inventory.selectedSlot = slot
    }

    @JvmStatic
    fun bestSlot(pos: BlockPos): Int {
        val state = mc.world!!.getBlockState(pos)
        if (state.getHardness(null, null) == -1.0f) {
            return -1
        }

        var slot = -1
        var dmg = 0.0f

        for (i in 0..8) {
            val stack = mc.player!!.inventory.getStack(i)
            if (stack.isEmpty) {
                continue
            }

            val toolDmg = stack.item.getMiningSpeedMultiplier(stack, state)
            if (toolDmg > dmg) {
                slot = i
                dmg = toolDmg
            }
        }

        return if (slot == -1) Paragon.inventoryManager.slot else slot
    }

}