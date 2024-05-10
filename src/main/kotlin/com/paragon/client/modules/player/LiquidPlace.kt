package com.paragon.client.modules.player

import com.paragon.backend.event.events.entity.RaycastEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import me.bush.eventbus.annotation.EventListener

/**
 * @author surge
 * @since 13/03/2023
 */
object LiquidPlace : Module("Liquid Place", "Allows you to place blocks on liquids", Category.PLAYER) {

    @EventListener
    fun onRaycast(event: RaycastEvent) {
        event.includeFluids = true
    }

}