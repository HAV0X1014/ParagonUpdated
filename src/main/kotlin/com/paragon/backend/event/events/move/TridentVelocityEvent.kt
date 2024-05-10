package com.paragon.backend.event.events.move

import me.bush.eventbus.event.Event

/**
 * @author surge
 * @since 24/02/2023
 */
class TridentVelocityEvent(var x: Double, var y: Double, var z: Double) : Event() {

    override fun isCancellable(): Boolean {
        return false
    }

}