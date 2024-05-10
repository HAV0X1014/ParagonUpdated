package com.paragon.client.modules.player

import com.paragon.Paragon
import com.paragon.backend.event.events.input.control.AttackBlockEvent
import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.event.events.render.GameRenderEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.backend.setting.Colour
import com.paragon.client.modules.player.AutoTool.bestSlot
import com.paragon.mixin.duck.IClientPlayerInteractionManager
import com.paragon.util.calculations.rotation.RotationUtil
import com.paragon.util.mc
import com.paragon.util.nullCheck
import com.paragon.util.player.PlayerUtil
import com.paragon.util.rendering.Renderer
import me.bush.eventbus.annotation.EventListener
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper

/**
 * @author aesthetical
 * @since 02/20/23
 */
object PacketMine : Module("Packet Mine", "Mines blocks with packets", Category.PLAYER) {

    private val percentage by float("Percentage", 0.95f, 0.05f, 0.0f..1.0f, "At what percentage to try and break the block")
    private val click by bool("Self Click", false, "If to require client input to finish breaking the block")
    private val rotate by bool("Rotate", false, "If to rotate towards the block you are breaking")
    private val render by bool("Render", true, "If to render the block progress")

    private var blockPos: BlockPos? = null
    private var direction: Direction? = null
    private var sentBreak = false
    private var sentStopBreak = false
    private var spoofItem = false
    private var override = false
    private var progress = 0.0

    override fun disable() {
        super.disable()

        if (!nullCheck()) {
            stopBlockBreaking()

            if (spoofItem) {
                Paragon.inventoryManager.sync()
                spoofItem = false
            }
        }

        override = false
        sentBreak = false
        blockPos = null
        direction = null
        progress = 0.0
    }

    @EventListener
    fun onGameRender(event: GameRenderEvent) {
        if (blockPos != null && render) {
            var box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
            box = box.offset(blockPos!!.x.toDouble(), blockPos!!.y.toDouble(), blockPos!!.z.toDouble())

            val center = box.center
            var bb = Box(center, center)

            val factor: Double = MathHelper.clamp(progress, 0.0, 1.0)
            bb = bb.expand(factor * 0.5, factor * 0.5, factor * 0.5)

            val color = (if (factor >= percentage) Colour(0, 255, 0, 100) else Colour(255, 0, 0, 100))
            Renderer.box(event.matrices, bb, color, Renderer.DrawMode.FILL)
            Renderer.box(event.matrices, bb, color, Renderer.DrawMode.LINES)
        }
    }

    @EventListener
    fun onTick(event: TickEvent) {
        if (blockPos != null && direction != null) {
            val blockState = mc.world!!.getBlockState(blockPos)
            if (blockState.material.isReplaceable || blockState.isAir || blockState.getHardness(null, null) == -1.0f) {
                stopBlockBreaking()

                sentStopBreak = false
                override = false

                if (spoofItem) {
                    Paragon.inventoryManager.sync()
                    spoofItem = false
                }
                return
            }

            val slot = bestSlot(blockPos!!)
            progress += getStrength(blockPos!!, slot)
            if (progress >= percentage || override) {

                if (Paragon.inventoryManager.slot != slot && progress >= percentage) {
                    spoofItem = true
                    Paragon.inventoryManager.swap(slot)
                }

                if (rotate) {
                    Paragon.rotationManager.submit(RotationUtil.calcAngleToBlock(blockPos!!, direction!!))
                }

                if (!sentStopBreak) {

                    if (click && !override) {
                        return
                    }

                    sentStopBreak = true
                    (mc.interactionManager as IClientPlayerInteractionManager).hookSendSequencedPacket(mc.world) {
                        return@hookSendSequencedPacket PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction, it)
                    }
                }
            }
        }
    }

    @EventListener
    fun onAttackBlock(event: AttackBlockEvent) {
        if (blockPos != null && event.blockPos != blockPos) {
            stopBlockBreaking()
        }

        if (blockPos != null && blockPos == event.blockPos) {
            event.isCancelled = true

            if (click && progress >= percentage) {
                override = true
            }

            return
        }

        val blockState = mc.world!!.getBlockState(event.blockPos)
        if (blockState.material.isReplaceable || blockState.isAir || blockState.getHardness(null, null) == -1.0f) {
            return
        }

        event.isCancelled = true

        blockPos = event.blockPos
        direction = event.direction

        if (!sentBreak) {
            sentBreak = true

            PlayerUtil.silentSwing(Hand.MAIN_HAND)
            (mc.interactionManager as IClientPlayerInteractionManager).hookSendSequencedPacket(mc.world) {
                return@hookSendSequencedPacket PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos, direction, it)
            }
        }
    }

    private fun stopBlockBreaking() {
        if (sentBreak) {
            mc.player!!.networkHandler.sendPacket(PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, blockPos, direction))
        }

        override = false
        progress = 0.0
        sentBreak = false
        blockPos = null
        direction = null
        sentStopBreak = false
    }

    private fun getStrength(blockPos: BlockPos, slot: Int): Double {
        val at = mc.world!!.getBlockState(blockPos)
        val held = mc.player!!.inventory.getStack(slot)
        val hardness = at.getHardness(null, null)
        if (hardness < 0.0f) {
            return 0.0
        }

        val s = getDestroySpeed(blockPos, slot)
        val f = (if (!held.isEmpty && held.isSuitableFor(at)) 30.0f else 100.0f).toDouble()
        return s / hardness / f
    }

    private fun getDestroySpeed(blockPos: BlockPos, slot: Int): Double {
        val at = mc.world!!.getBlockState(blockPos)
        val held = mc.player!!.inventory.getStack(slot)

        var breakSpeed = 1.0f
        if (!held.isEmpty) {
            breakSpeed *= held.getMiningSpeedMultiplier(at)
        }

        if (breakSpeed > 1.0f) {
            val effMod: Int = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, held)
            if (effMod > 0) {
                val mod = effMod * effMod + 1.0f
                breakSpeed += if (!held.isSuitableFor(at)) {
                    mod * 0.08f
                } else {
                    mod
                }
            }
        }

        if (mc.player!!.hasStatusEffect(StatusEffects.HASTE)) {
            val amp = mc.player!!.getStatusEffect(StatusEffects.HASTE)!!.amplifier
            if (amp > 0) {
                breakSpeed *= 1.0f + (amp + 1.0f) * 0.2f
            }
        }

        if (mc.player!!.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            val amp = mc.player!!.getStatusEffect(StatusEffects.MINING_FATIGUE)!!.amplifier
            if (amp > 0) {
                breakSpeed *= 1.0f + (amp + 1.0f) * 0.2f
            }
        }

        if (mc.player!!.isSubmergedInWater && !EnchantmentHelper.hasAquaAffinity(mc.player)) {
            breakSpeed /= 5.0f
        }

        return breakSpeed.toDouble()
    }
}