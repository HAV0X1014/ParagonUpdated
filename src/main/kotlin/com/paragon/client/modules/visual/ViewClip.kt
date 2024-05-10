package com.paragon.client.modules.visual

import com.paragon.backend.event.events.render.ClipToSpaceEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import me.bush.eventbus.annotation.EventListener

/**
 * @author aesthetical
 * @since 02/19/23
 */
object ViewClip : Module("View Clip", "Clips your camera out of bounds", Category.VISUAL) {

    private val distance by double("Distance", 3.0, 0.5, 1.0..50.0, "The camera clip distance")

    @EventListener
    fun onClipToSpace(event: ClipToSpaceEvent) {
        event.distance = distance
        event.isCancelled = true
    }

}