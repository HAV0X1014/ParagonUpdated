package com.paragon.client.modules.player

import com.paragon.Paragon
import com.paragon.backend.event.EventEra
import com.paragon.backend.event.events.move.MoveUpdateEvent
import com.paragon.backend.managers.placement.PlacementData
import com.paragon.backend.managers.placement.Swing
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.mixin.duck.IVec3d
import com.paragon.util.calculations.MoveUtil.getVanillaJumpVelocity
import com.paragon.util.calculations.rotation.RotationUtil
import com.paragon.util.mc
import com.paragon.util.nullCheck
import me.bush.eventbus.annotation.EventListener
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

/**
 * @author aesthetical
 * @since 02/17/23
 */
object BlockFly : Module("Block Fly", "Rapidly places blocks under your feet", Category.PLAYER) {

    private val rotate by bool("Rotate", true, "If to rotate where you're placing")
    private val swing by enum("Swing", Swing.CLIENT, "How to swing")
    private val autoSwap by enum("Auto Swap", AutoSwap.SERVER, "Automatically swap to a block slot")
    private val tower by bool("Tower", true, "If to tower")

    private var prev: PlaceResult? = null
    private var curr: PlaceResult? = null

    override fun disable() {
        curr = null
        prev = curr

        if (!nullCheck()) {
            Paragon.inventoryManager.sync()
        }
    }

    @EventListener(recieveCancelled = true)
    fun onMoveUpdate(event: MoveUpdateEvent) {
        prev = curr
        curr = next()

        if (rotate && prev != null) {
            Paragon.rotationManager.submit(RotationUtil.calcAngleToBlock(prev!!.pos, prev!!.direction))
        }

        if (curr == null) {
            return
        }

        if (event.era == EventEra.POST) {
            when (autoSwap) {
                AutoSwap.NONE -> {
                    if (mc.player!!.mainHandStack.item !is BlockItem) {
                        return
                    }
                }

                AutoSwap.CLIENT, AutoSwap.SERVER -> {
                    val slot = nextSlot

                    if (slot == -1) {
                        return
                    }

                    if (autoSwap == AutoSwap.CLIENT) {
                        mc.player!!.inventory.selectedSlot = slot
                    } else {
                        Paragon.inventoryManager.swap(slot)
                    }
                }
            }

            Paragon.placementManager.submit(curr!!.pos, PlacementData(mc.player!!.inventory.selectedSlot, curr!!.direction, swing).accept {
                if (tower && mc.options.jumpKey.isPressed) {
                    (mc.player!!.velocity as IVec3d).setY(getVanillaJumpVelocity().toDouble())

                    if (mc.player!!.age % 4 == 0 || mc.player!!.isOnGround) {
                        (mc.player!!.velocity as IVec3d).setY(getVanillaJumpVelocity().toDouble())
                    }
                }
            })
        }
    }

    private val nextSlot: Int
        get() {
            var slot: Int = mc.player!!.inventory.selectedSlot
            var count = 0

            if (mc.player!!.inventory.getStack(slot).item is BlockItem) {
                count = mc.player!!.inventory.getStack(slot).count
            } else {
                slot = -1
            }

            for (i in 0..8) {
                val stack: ItemStack = mc.player!!.inventory.getStack(i)

                if (stack.item is BlockItem && stack.count > count) {
                    slot = i
                    count = stack.count
                }
            }

            return slot
        }

    private operator fun next(): PlaceResult? {
        val pos: BlockPos = BlockPos(mc.player!!.pos).down()

        for (direction in Direction.values()) {
            val n = pos.offset(direction)

            if (!mc.world!!.getBlockState(n).material.isReplaceable) {
                return PlaceResult(n, direction.opposite)
            }
        }

        for (direction in Direction.values()) {
            val n = pos.offset(direction)

            if (mc.world!!.getBlockState(n).material.isReplaceable) {
                for (d in Direction.values()) {
                    val nn = n.offset(d)

                    if (!mc.world!!.getBlockState(nn).material.isReplaceable) {
                        return PlaceResult(nn, d.opposite)
                    }
                }
            }
        }
        return null
    }

    private data class PlaceResult(val pos: BlockPos, val direction: Direction)

    enum class AutoSwap {
        NONE,
        CLIENT,
        SERVER
    }

}