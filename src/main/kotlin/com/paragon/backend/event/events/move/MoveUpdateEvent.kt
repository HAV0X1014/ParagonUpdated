package com.paragon.backend.event.events.move

import com.paragon.backend.event.EventEra
import me.bush.eventbus.event.Event

/**
 * @author aesthetical
 * @since 02/17/23
 */
class MoveUpdateEvent(
    @JvmField val era: EventEra,
    @JvmField var x: Double,
    @JvmField var y: Double,
    @JvmField var z: Double,
    @JvmField var yaw: Float,
    @JvmField var pitch: Float,
    @JvmField var onGround: Boolean
) : Event() {
    override fun isCancellable(): Boolean {
        return true
    }
}