package com.paragon.client.modules.player

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.calculations.Timer
import com.paragon.util.inventory.InventoryUtil
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType

/**
 * @author aesthetical
 * @since 02/18/23
 */
object Replenish : Module("Replenish", "Automatically replenishes your hotbar", Category.PLAYER) {

    private val delay = double("Delay", 0.1, 0.1, 0.0..3.5, "How long before replenishing the next item")
    private val percentage = float("Percentage", 75.0f, 0.1f, 1.0f..99.0f, "The percentage of item depletion before replenishing")

    private val hotbarCache: MutableMap<Int, ItemStack> = HashMap()
    private val timer = Timer()

    override fun disable() {
        super.disable()
        hotbarCache.clear()
    }

    @EventListener
    fun onTick(event: TickEvent?) {
        for (i in 0..8) {
            val stack: ItemStack = mc.player!!.inventory.getStack(i)

            if (!stack.isEmpty) {
                hotbarCache[i] = stack
            }
        }

        if (timer.elapsed((delay.value * 1000.0).toLong().toDouble())) {
            for (i in 0..8) {
                val stack: ItemStack = mc.player!!.inventory.getStack(i)
                val percent = stack.count.toFloat() / stack.maxCount.toFloat() * 100.0f

                if (percent < percentage.value && replenishItem(i)) {
                    timer.reset()
                    break
                }
            }
        }
    }

    private fun replenishItem(slot: Int): Boolean {
        if (!hotbarCache.containsKey(slot)) {
            return false
        }

        val itemStack = hotbarCache[slot]
        var s = -1
        var count = 0

        for (i in 9..35) {
            val stack: ItemStack = mc.player!!.inventory.getStack(i)

            if (stack.isEmpty) {
                continue
            }

            if (InventoryUtil.getStackName(stack) != InventoryUtil.getStackName(itemStack!!)) {
                continue
            }

            if (itemStack.item is BlockItem && stack.item is BlockItem && (stack.item as BlockItem).block == (itemStack.item as BlockItem).block) {
                s = i
                count = stack.count
                break
            }

            if (itemStack.item == stack.item) {
                s = i
                count = stack.count
                break
            }
        }

        if (s == -1) {
            return false
        }

        val placeBack = count + itemStack!!.count > itemStack.maxCount
        val syncId: Int = mc.player!!.currentScreenHandler.syncId

        mc.interactionManager!!.clickSlot(syncId, s, 0, SlotActionType.PICKUP, mc.player)
        mc.interactionManager!!.clickSlot(syncId, InventoryUtil.normalize(slot), 0, SlotActionType.PICKUP, mc.player)

        if (placeBack) {
            mc.interactionManager!!.clickSlot(syncId, s, 0, SlotActionType.PICKUP, mc.player)
        }

        return true
    }

}