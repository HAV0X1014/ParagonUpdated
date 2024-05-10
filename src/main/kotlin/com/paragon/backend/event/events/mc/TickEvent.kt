package com.paragon.backend.event.events.mc

import me.bush.eventbus.event.Event

/**
 * @author surge
 * @since 11/02/2023
 */
class TickEvent : Event() {

    override fun isCancellable() = false

}