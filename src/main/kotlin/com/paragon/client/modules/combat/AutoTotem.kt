package com.paragon.client.modules.combat

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.calculations.Timer
import com.paragon.util.inventory.InventoryUtil
import com.paragon.util.inventory.InventoryUtil.normalize
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.SwordItem
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import kotlin.math.roundToInt

/**
 * @author aesthetical
 * @since 02/25/23
 */
object AutoTotem : Module("Auto Totem", "Replaces items in your offhand", Category.COMBAT) {

    private val item by enum("Item", ItemType.TOTEM, "The item type")
    private val health by float("Health", 16.0f, 0.1f, 1.0f..19.0f, "The health to swap to a totem")
    private val delay by double("Delay", 0.1, 0.1, 0.0..3.5, "How long before replenishing the offhand slot")
    private val gapple by bool("Gapple", true, "If to offhand gapple")

    private val timer = Timer()

    override val info = { getDisplayInfo().toString() }

    @EventListener
    fun onTick(event: TickEvent) {
        val fallDist = getMaxFallDist()
        val items = if (mc.player!!.health < health || (fallDist > 0.0f && fallDist <= health)) {
            ItemType.TOTEM.items
        } else if (mc.options.useKey.isPressed && gapple && InventoryUtil.hasStackIn(Hand.MAIN_HAND, SwordItem::class.java)) {
            ItemType.GAPPLE.items
        } else {
            item.items
        }

        if (!InventoryUtil.hasStackIn(Hand.OFF_HAND, items) && timer.elapsed(delay * 1000.0)) {
            timer.reset()
            replaceInOffhand(items)
        }
    }

    private fun replaceInOffhand(items: Array<out Item>) {
        if (InventoryUtil.hasStackIn(Hand.OFF_HAND, items)) {
            return
        }

        var slot = -1
        for (i in 0..36) {
            val itemStack = mc.player!!.inventory.getStack(i)
            if (!itemStack.isEmpty) {
                for (item in items) {
                    if (itemStack.item == item) {
                        slot = i
                        break
                    }
                }
            }
        }

        if (slot == -1) {
            return
        }

        val hadInOffhand = !mc.player!!.getStackInHand(Hand.OFF_HAND).isEmpty
        val syncId = mc.player!!.currentScreenHandler.syncId
        mc.interactionManager!!.clickSlot(syncId, normalize(slot), 0, SlotActionType.PICKUP, mc.player)
        mc.interactionManager!!.clickSlot(syncId, 45, 0, SlotActionType.PICKUP, mc.player)
        if (hadInOffhand) {
            mc.interactionManager!!.clickSlot(syncId, normalize(slot), 0, SlotActionType.PICKUP, mc.player)
        }

    }

    private fun getMaxFallDist(): Float {
        val multiplier = if (isBlockUnderSafe()) 0.0f else 1.0f
        var reduction = 0.0f
        if (mc.player!!.hasStatusEffect(StatusEffects.JUMP_BOOST)) {
            reduction = (mc.player!!.getStatusEffect(StatusEffects.JUMP_BOOST)!!.amplifier + 1).toFloat()
        }

        val dmg = MathHelper.ceil((getFirstBlockPosY() - 3.0f - reduction) * multiplier).toFloat()
        return (mc.player!!.health - dmg).coerceAtLeast(0.0f)
    }

    private fun getFirstBlockPosY(): Double {
        for (y in 1 until 256 - (mc.player!!.y + 1.0).roundToInt()) {
            val block: Block = mc.world!!.getBlockState(BlockPos(
                    mc.player!!.x + mc.player!!.velocity.x * 1.5,
                    (mc.player!!.y - y.toFloat()),
                    mc.player!!.z + mc.player!!.velocity.z * 1.5)).block
            if (block != Blocks.AIR) {
                return mc.player!!.y - y
            }
        }

        return -1.0
    }

    private fun isBlockUnderSafe(): Boolean {
        for (y in 1 until 256 - (mc.player!!.y + 1.0).roundToInt()) {
            val block: Block = mc.world!!.getBlockState(BlockPos(
                    mc.player!!.x + mc.player!!.velocity.x * 1.5,
                    (mc.player!!.y - y.toFloat()),
                    mc.player!!.z + mc.player!!.velocity.z * 1.5)).block
            if (block == Blocks.SLIME_BLOCK && !mc.player!!.isSneaking || block == Blocks.COBWEB || block == Blocks.WATER) {
                return true
            }
        }

        return false
    }

    private fun getDisplayInfo(): Int {
        val offhandStack = mc.player!!.getStackInHand(Hand.OFF_HAND)
        return getItemCount(offhandStack.item)
    }

    private fun getItemCount(vararg items: Item): Int {
        var count = 0
        for (i in 0..36) {
            val itemStack = mc.player!!.inventory.getStack(i)
            if (!itemStack.isEmpty) {
                for (item in items) {
                    if (itemStack.item == item) {
                        count += itemStack.count
                    }
                }
            }
        }

        if (!mc.player!!.getStackInHand(Hand.OFF_HAND).isEmpty) {
            val offhandStack = mc.player!!.getStackInHand(Hand.OFF_HAND)
            for (item in items) {
                if (offhandStack.item == item) {
                    count += offhandStack.count
                }
            }
        }

        return count
    }


    enum class ItemType(vararg val items: Item) {
        TOTEM(Items.TOTEM_OF_UNDYING),
        CRYSTAL(Items.END_CRYSTAL),
        GAPPLE(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE)
    }
}