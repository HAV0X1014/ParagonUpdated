package com.paragon.backend.event.events.mc

import me.bush.eventbus.event.Event

/**
 * @author surge
 * @since 20/02/2023
 */
class ShutdownEvent : Event() {

    override fun isCancellable(): Boolean {
        return false
    }

}