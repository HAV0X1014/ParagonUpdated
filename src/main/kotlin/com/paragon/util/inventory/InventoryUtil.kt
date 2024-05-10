package com.paragon.util.inventory

import com.paragon.util.mc
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Hand

/**
 * @author aesthetical
 * @since 02/18/23
 */
object InventoryUtil {

    @JvmField val HOTBAR = 0..8

    // fuck mojang
    fun getStackName(stack: ItemStack): String {
        val compound: NbtCompound? = stack.getSubNbt("display")
        return if (compound == null || !compound.contains("Name")) "" else compound.getString("Name")
    }

    fun hasStackIn(hand: Hand, items: Array<out Item>): Boolean {
        val itemStack = mc.player!!.getStackInHand(hand)

        for (item in items) {
            if (!itemStack.isEmpty && itemStack.item == item) {
                return true
            }
        }

        return false
    }

    fun hasStackIn(hand: Hand, vararg items: Class<*>): Boolean {
        val itemStack = mc.player!!.getStackInHand(hand)

        for (item in items) {
            if (!itemStack.isEmpty && item.isInstance(itemStack.item)) {
                return true
            }
        }

        return false
    }

    fun holding(item: Item): Boolean {
        return mc.player!!.isHolding(item)
    }

    fun normalize(slot: Int): Int {
        return if (slot < 9) slot + 36 else slot
    }

    fun find(range: IntRange, condition: (ItemStack) -> Boolean): Int {
        var slot: Int = mc.player!!.inventory.selectedSlot
        var count = 0

        if (condition(mc.player!!.inventory.getStack(slot))) {
            count = mc.player!!.inventory.getStack(slot).count
        } else {
            slot = -1
        }

        for (i in range) {
            val stack: ItemStack = mc.player!!.inventory.getStack(i)

            if (condition(stack) && stack.count > count) {
                slot = i
                count = stack.count
            }
        }

        return slot
    }

}