package com.paragon.backend.event.events.input.io

import me.bush.eventbus.event.Event

/**
 * @author aesthetical
 * @since 02/17/23
 */
class MouseEvent(@JvmField val button: Int, @JvmField val action: Int) : Event() {

    override fun isCancellable(): Boolean {
        return false
    }
}