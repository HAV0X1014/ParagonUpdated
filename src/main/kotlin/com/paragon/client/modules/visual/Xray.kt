package com.paragon.client.modules.visual

import com.paragon.backend.event.events.render.BlockAmbientLightLevelEvent
import com.paragon.backend.event.events.render.DrawSideOfBlockEvent
import com.paragon.backend.event.events.render.GammaModifyEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import com.paragon.util.nullCheck
import me.bush.eventbus.annotation.EventListener
import net.minecraft.block.Block
import net.minecraft.block.Blocks

/**
 * @author aesthetical
 * @since 02/20/23
 */
object Xray : Module("Xray", "Lets you see ores through blocks", Category.VISUAL) {

    @JvmStatic
    val blocks = mutableListOf<Block>()

    init {
        // TODO: Make this load from a config

        blocks += Blocks.COAL_ORE
        blocks += Blocks.COPPER_ORE
        blocks += Blocks.IRON_ORE
        blocks += Blocks.LAPIS_ORE
        blocks += Blocks.GOLD_ORE
        blocks += Blocks.EMERALD_ORE
        blocks += Blocks.DIAMOND_ORE
        blocks += Blocks.REDSTONE_ORE
        blocks += Blocks.NETHER_QUARTZ_ORE
        blocks += Blocks.ANCIENT_DEBRIS
        blocks += Blocks.DEEPSLATE_COAL_ORE
        blocks += Blocks.DEEPSLATE_COPPER_ORE
        blocks += Blocks.DEEPSLATE_IRON_ORE
        blocks += Blocks.DEEPSLATE_LAPIS_ORE
        blocks += Blocks.DEEPSLATE_GOLD_ORE
        blocks += Blocks.DEEPSLATE_EMERALD_ORE
        blocks += Blocks.DEEPSLATE_DIAMOND_ORE
        blocks += Blocks.DEEPSLATE_REDSTONE_ORE
    }

    override fun enable() {
        super.enable()
        if (!nullCheck()) {
            mc.worldRenderer.reload()
        }
    }

    override fun disable() {
        super.disable()
        if (!nullCheck()) {
            mc.worldRenderer.reload()
        }
    }

    @EventListener
    fun onDrawSideOfBlock(event: DrawSideOfBlockEvent) {
        event.drawSide = blocks.contains(event.block)
        event.isCancelled = true
    }

    @EventListener
    fun onBlockAmbientLightLevel(event: BlockAmbientLightLevelEvent) {
        event.lightLevel = 100.0f
        event.isCancelled = true
    }

    @EventListener
    fun onGammaModify(event: GammaModifyEvent) {
        event.gamma = 100.0f
        event.isCancelled = true
    }
}