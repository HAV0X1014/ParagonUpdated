package com.paragon.backend.event.events.entity

import com.paragon.backend.event.EventEra
import me.bush.eventbus.event.Event
import net.minecraft.entity.LivingEntity

/**
 * @author aesthetical
 * @since 02/19/23
 */
class EntityTickEvent(val era: EventEra, val entity: LivingEntity) : Event() {

    override fun isCancellable(): Boolean {
        return false
    }

}