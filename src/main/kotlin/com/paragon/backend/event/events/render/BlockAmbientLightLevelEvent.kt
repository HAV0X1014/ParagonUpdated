package com.paragon.backend.event.events.render

import me.bush.eventbus.event.Event
import net.minecraft.util.math.BlockPos

/**
 * @author aesthetical
 * @since 02/20/23
 */
class BlockAmbientLightLevelEvent(val pos: BlockPos, var lightLevel: Float) : Event() {
    override fun isCancellable(): Boolean {
        return true
    }
}