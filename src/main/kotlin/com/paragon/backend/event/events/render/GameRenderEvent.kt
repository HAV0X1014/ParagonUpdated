package com.paragon.backend.event.events.render

import me.bush.eventbus.event.Event
import net.minecraft.client.util.math.MatrixStack

/**
 * @author surge, aesthetical
 * @since 12/02/2023
 */
class GameRenderEvent(@JvmField val matrices: MatrixStack, @JvmField val tickDelta: Float) : Event() {

    override fun isCancellable(): Boolean {
        return false
    }
}