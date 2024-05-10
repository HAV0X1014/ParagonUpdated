package com.paragon.backend.event.events.entity

import me.bush.eventbus.event.Event

/**
 * @author surge
 * @since 13/03/2023
 */
class RaycastEvent(var distance: Double, var tickDelta: Float, var includeFluids: Boolean) : Event() {

    override fun isCancellable() = false

}