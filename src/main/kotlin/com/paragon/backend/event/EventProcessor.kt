package com.paragon.backend.event

import com.paragon.Paragon
import com.paragon.Paragon.Companion.version
import com.paragon.backend.event.events.mc.ShutdownEvent
import com.paragon.backend.event.events.mc.TitleEvent
import com.paragon.util.rendering.NVGWrapper
import me.bush.eventbus.annotation.EventListener

/**
 * @author surge
 * @since 19/02/2023
 */
class EventProcessor {

    init {
        Paragon.bus.subscribe(this)
    }

    @EventListener
    fun onTitle(event: TitleEvent) {
        event.title = "Paragon v" + version + " | " + String(TitleEvent.funnyArrays[(Math.random() * TitleEvent.funnyArrays.size).toInt()])
    }

    @EventListener
    fun onShutdown(event: ShutdownEvent) {
        NVGWrapper.terminate()
        Paragon.moduleManager.save("current")
    }

}