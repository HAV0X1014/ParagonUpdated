package com.paragon.backend.event.events.render

import me.bush.eventbus.event.Event

/**
 * @author aesthetical
 * @since 02/19/23
 */
class ClipToSpaceEvent(var distance: Double) : Event() {
    override fun isCancellable(): Boolean {
        return true
    }
}