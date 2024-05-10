package com.paragon.backend.event.events.render

import me.bush.eventbus.event.Event

class GammaModifyEvent(@JvmField var gamma: Float) : Event() {

    override fun isCancellable(): Boolean {
        return true
    }
}