package com.paragon.client.modules.visual

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.event.events.render.GammaModifyEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import com.paragon.util.nullCheck
import me.bush.eventbus.annotation.EventListener
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects

/**
 * @author KassuK, aesthetical
 * @since 02/17/23
 */
object FullBright : Module("Full Bright", "Makes the game brighter", Category.VISUAL) {

    private var gavePotion = false

    private val mode by enum("Mode", Mode.GAMMA, "How to brighten up your game")

    override val info = { mode.name }

    override fun disable() {
        if (!nullCheck() && gavePotion) {
            mc.player!!.removeStatusEffect(StatusEffects.NIGHT_VISION)
        }

        gavePotion = false
    }

    @EventListener
    fun onTick(event: TickEvent?) {
        if (mode == Mode.EFFECT) {
            if (mc.player!!.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                return
            }

            gavePotion = true

            mc.player!!.addStatusEffect(StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 1))
        }
    }

    @EventListener
    fun onGammaModify(event: GammaModifyEvent) {
        if (mode == Mode.GAMMA) {
            event.gamma = 100.0f
            event.isCancelled = true
        }
    }

    enum class Mode {
        GAMMA,
        EFFECT
    }

}