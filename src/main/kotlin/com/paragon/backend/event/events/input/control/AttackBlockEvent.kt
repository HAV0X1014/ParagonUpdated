package com.paragon.backend.event.events.input.control

import me.bush.eventbus.event.Event
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

/**
 * @author aesthetical
 * @since 02/20/23
 */
class AttackBlockEvent(val blockPos: BlockPos, val direction: Direction) : Event() {
    override fun isCancellable(): Boolean {
        return true
    }
}