package com.paragon.client.modules.visual

import com.paragon.backend.event.events.render.FPSLimitEvent
import com.paragon.backend.event.events.render.PreGameRenderEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener

/**
 * @author surge
 * @since 24/02/2023
 */
object UnfocusedCPU : Module("Unfocused CPU", "Stops world rendering when the window isn't focused", Category.VISUAL) {

    private var limit by int("Limit", 5, 1, 1..60, "The FPS limit to apply when the window isn't focused")

    @EventListener
    fun onPreGameRender(event: PreGameRenderEvent) {
        if (!mc.isWindowFocused) {
            event.cancel()
        }
    }

    @EventListener
    fun onFPSLimit(event: FPSLimitEvent) {
        if (!mc.isWindowFocused) {
            event.cancel()
            event.limit = limit
        }
    }

}