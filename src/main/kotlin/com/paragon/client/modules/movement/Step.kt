package com.paragon.client.modules.movement

import com.paragon.Paragon
import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import com.paragon.util.nullCheck
import me.bush.eventbus.annotation.EventListener

/**
 * @author surge
 * @since 11/02/2023
 */
object Step : Module("Step", "Lets you instantly walk up blocks", Category.MOVEMENT) {

    private val height by float("Height", 1.0f, 0.1f, 0.5f..2.5f, "The maximum height you can step up")

    override val info = { height.toString() }

    override fun enable() {
        super.enable()
        Paragon.baritoneManager.set("assumeStep", true)
    }

    override fun disable() {
        Paragon.baritoneManager.set("assumeStep", false)
        if (nullCheck()) {
            return
        }

        mc.player!!.stepHeight = 0.6f
    }

    @EventListener
    fun onTick(event: TickEvent) {
        if (nullCheck()) {
            return
        }

        mc.player!!.stepHeight = height
    }

}