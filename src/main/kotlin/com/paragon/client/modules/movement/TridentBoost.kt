package com.paragon.client.modules.movement

import com.paragon.backend.event.events.move.TridentVelocityEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import me.bush.eventbus.annotation.EventListener

/**
 * @author surge
 * @since 24/02/2023
 */
object TridentBoost : Module("Trident Boost", "Adds an additional boost when you use a trident", Category.MOVEMENT) {

    private val multiplier by double("Multiplier", 1.5, 0.1, 0.1..3.0, "How much your velocity is multiplied by")

    @EventListener
    fun onTridentVelocity(event: TridentVelocityEvent) {
        event.x *= multiplier
        event.y *= multiplier
        event.z *= multiplier
    }

}