package com.paragon.backend.event.events.input.control

import me.bush.eventbus.event.Event
import net.minecraft.client.network.ClientPlayerEntity

class SneakSlowdownEvent(@JvmField val entity: ClientPlayerEntity) : Event() {

    override fun isCancellable(): Boolean {
        return true
    }
}