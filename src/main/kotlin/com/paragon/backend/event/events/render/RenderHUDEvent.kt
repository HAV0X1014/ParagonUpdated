package com.paragon.backend.event.events.render

import me.bush.eventbus.event.Event
import net.minecraft.client.util.math.MatrixStack

/**
 * @author aesthetical
 * @since 02/17/23
 */
class RenderHUDEvent(val stack: MatrixStack, val partialTicks: Float, @JvmField val width: Float, val height: Float) : Event() {

    override fun isCancellable(): Boolean {
        return false
    }
}