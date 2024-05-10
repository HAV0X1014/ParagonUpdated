package com.paragon.backend.event.events.render

import me.bush.eventbus.event.Event

/**
 * @author surge
 * @since 24/02/2023
 */
class PreGameRenderEvent : Event() {

    override fun isCancellable(): Boolean {
        return true
    }

}