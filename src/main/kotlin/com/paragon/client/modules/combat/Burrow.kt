package com.paragon.client.modules.combat

import com.paragon.Paragon
import com.paragon.backend.event.EventEra
import com.paragon.backend.event.events.move.MoveUpdateEvent
import com.paragon.backend.event.events.net.PacketEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.calculations.rotation.RotationUtil
import com.paragon.util.mc
import com.paragon.util.nullCheck
import com.paragon.util.player.PlayerUtil
import me.bush.eventbus.annotation.EventListener
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import java.lang.Double.isNaN

/**
 * @author aesthetical
 * @since 02/20/23
 */
object Burrow : Module("Burrow", "Lags you back into a block", Category.COMBAT) {

    private val vanillaJumpHeights = listOf(0.419999986886978, 0.7531999805212015, 1.001335979112147, 1.166109260938214)
    private val validBlocks = listOf(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST)

    private val rotate by bool("Rotate", true, "If to rotate towards the block you're placing on")
    private val swing by bool("Swing", true, "If to swing client side")
    private val autoSwap by enum("Auto Swap", AutoSwap.SERVER, "How to swap to the block")
    private val replace by bool("Replace", false, "Keeps the module on and automatically replaces the burrow block")

    private var startY = Double.NaN
    private var burrowing = false
    private var swapped = false;

    override fun disable() {
        super.disable()
        startY = Double.NaN
        burrowing = false

        if (swapped && !nullCheck()) {
            Paragon.inventoryManager.sync()
        }

        swapped = false
    }

    @EventListener
    fun onMoveUpdate(event: MoveUpdateEvent) {

        if (event.era == EventEra.POST) {
            if (isNaN(startY)) {
                startY = mc.player!!.y
            }

            if (mc.player!!.y - startY > 0.2) {
                toggle()
                return
            }

            val blockPos = BlockPos(mc.player!!.pos)
            if (mc.world!!.getBlockState(blockPos).material.isReplaceable) {
                burrow(blockPos)
                if (!replace) {
                    toggle()
                }
            }
        }
    }

    @EventListener
    fun onPacketInbound(event: PacketEvent.Inbound) {
        if (nullCheck() || !replace) {
            return
        }

        if (event.packet is BlockUpdateS2CPacket) {
            val packet = event.packet
            if (packet.pos.equals(BlockPos(mc.player!!.pos)) && packet.state.isReplaceable) {
                burrow(packet.pos)
            }
        }
    }

    private fun burrow(blockPos: BlockPos) {
        if (burrowing) {
            return
        }

        burrowing = true

        var result: PlaceResult? = null
        for (facing in Direction.values()) {
            val n = blockPos.offset(facing)
            if (!mc.world!!.getBlockState(n).material.isReplaceable) {
                result = PlaceResult(n, facing.opposite)
                break
            }
        }

        if (result == null) {
            burrowing = false
            return
        }

        var slot = -1
        for (i in 0..8) {
            val itemStack = mc.player!!.inventory.getStack(i)
            if (!itemStack.isEmpty && itemStack.item is BlockItem) {
                val block = (itemStack.item as BlockItem).block
                if (validBlocks.contains(block)) {
                    slot = i
                    break
                }
            }
        }

        if (slot == -1) {
            burrowing = false
            return
        }

        if (rotate) {
            val rotations = RotationUtil.calcAngleToBlock(result.pos, result.direction)
            Paragon.rotationManager.submit(rotations)
            mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.LookAndOnGround(rotations[0], rotations[1], mc.player!!.isOnGround))
        }

        var oldSlot = -1
        swapped = true
        if (autoSwap == AutoSwap.CLIENT) {
            oldSlot = mc.player!!.inventory.selectedSlot
            mc.player!!.inventory.selectedSlot = slot
        } else {
            Paragon.inventoryManager.swap(slot)
        }

        for (value in vanillaJumpHeights) {
            mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(
                    mc.player!!.x, mc.player!!.y + value, mc.player!!.z, false))
        }

        val actionResult = mc.interactionManager!!.interactBlock(mc.player, Hand.MAIN_HAND,
                BlockHitResult(
                        Vec3d(result.pos.x.toDouble(), result.pos.y.toDouble(), result.pos.z.toDouble())
                                .add(0.5, 0.5, 0.5),
                        result.direction, result.pos, false))

        if (actionResult.isAccepted) {
            if (swing) {
                mc.player!!.swingHand(Hand.MAIN_HAND)
            } else {
                PlayerUtil.silentSwing(Hand.MAIN_HAND)
            }
        }

        // TODO: Stop this flagging!
        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.PositionAndOnGround(
                mc.player!!.x, mc.player!!.y + 2.3, mc.player!!.z, false))

        burrowing = false

        if (autoSwap == AutoSwap.CLIENT) {
            mc.player!!.inventory.selectedSlot = oldSlot
        } else {
            Paragon.inventoryManager.sync()
        }

        swapped = false
    }

    private data class PlaceResult(val pos: BlockPos, val direction: Direction)

    enum class AutoSwap {
        CLIENT, SERVER
    }

}