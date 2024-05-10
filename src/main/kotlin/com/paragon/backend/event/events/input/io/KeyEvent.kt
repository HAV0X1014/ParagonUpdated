package com.paragon.backend.event.events.input.io

import me.bush.eventbus.event.Event

/**
 * @author surge
 * @since 11/02/2023
 */
class KeyEvent(@JvmField val code: Int, @JvmField val action: Int) : Event() {

    override fun isCancellable(): Boolean {
        return false
    }
}