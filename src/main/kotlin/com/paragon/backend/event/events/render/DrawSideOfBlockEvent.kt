package com.paragon.backend.event.events.render

import me.bush.eventbus.event.Event
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

/**
 * @author aesthetical
 * @since 02/20/23
 */
class DrawSideOfBlockEvent(val block: Block, val pos: BlockPos, val state: BlockState, var drawSide: Boolean) : Event() {
    override fun isCancellable(): Boolean {
        return true
    }
}