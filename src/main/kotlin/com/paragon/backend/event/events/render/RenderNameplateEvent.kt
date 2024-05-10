package com.paragon.backend.event.events.render

import me.bush.eventbus.event.Event
import net.minecraft.client.network.AbstractClientPlayerEntity

/**
 * @author aesthetical
 * @since 02/19/23
 */
class RenderNameplateEvent(val entity: AbstractClientPlayerEntity) : Event() {

    override fun isCancellable(): Boolean {
        return true
    }

}