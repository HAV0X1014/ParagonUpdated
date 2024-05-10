package com.paragon.client.modules.player

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.mixin.duck.IMinecraftClient
import com.paragon.mixin.duck.IRenderTickCounter
import com.paragon.util.mc
import com.paragon.util.nullCheck
import me.bush.eventbus.annotation.EventListener

/**
 * @author aesthetical
 * @since 02/17/23
 */
object GameSpeed : Module("Game Speed", "makes ur game go zoom zoom verus airlines", Category.PLAYER) {

    private val speed by float("Speed", 1.0f, 0.01f, 0.1f..20.0f, "The timer speed")

    override val info = { speed.toString() }

    override fun disable() {
        if (!nullCheck()) {
            setTimerSpeed(1.0f)
        }
    }

    @EventListener
    fun onTick(event: TickEvent?) {
        setTimerSpeed(speed)
    }

    @JvmStatic
    fun setTimerSpeed(speed: Float) {
        ((mc as IMinecraftClient).renderTickCounter as IRenderTickCounter).tickLength = 50.0f / speed
    }

}