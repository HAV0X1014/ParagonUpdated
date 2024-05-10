package com.paragon.backend.event.events.entity

import me.bush.eventbus.event.Event
import net.minecraft.util.hit.HitResult

/**
 * @author surge
 * @since 28/02/2023
 */
class EntityTraceEvent(val result: HitResult?) : Event() {

    override fun isCancellable() = true

}