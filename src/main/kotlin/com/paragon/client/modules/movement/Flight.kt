package com.paragon.client.modules.movement

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.mixin.duck.IVec3d
import com.paragon.util.calculations.MoveUtil.moving
import com.paragon.util.calculations.MoveUtil.strafe
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener

/**
 * @author surge, aesthetical
 * @since 11/02/2023
 */
object Flight : Module("Flight", "Lets you fly in survival mode", Category.MOVEMENT) {

    private val mode by enum("Mode", Mode.MOTION, "How to fly")
    private val speed by float("Speed", 0.07f, 0.01f, 0.01f..0.5f, "How fast you fly")

    override val info = { mode.toString() }

    override fun disable() {
        if (mode == Mode.CREATIVE) {
            mc.player!!.abilities.flying = false
            mc.player!!.abilities.flySpeed = 0.05f

            if (!mc.player!!.abilities.creativeMode) {
                mc.player!!.abilities.allowFlying = false
            }
        }
    }

    @EventListener
    fun onTick(event: TickEvent?) {
        when (mode) {
            Mode.MOTION -> {
                if (moving()) {
                    val strafe = strafe(speed * 10.0)
                    (mc.player!!.velocity as IVec3d).set(strafe[0], strafe[1])
                } else {
                    (mc.player!!.velocity as IVec3d).set(0.0, 0.0)
                }

                if (mc.options.jumpKey.isPressed) {
                    (mc.player!!.velocity as IVec3d).setY(speed * 10.0)
                } else if (mc.options.sneakKey.isPressed) {
                    (mc.player!!.velocity as IVec3d).setY(-speed * 10.0)
                } else {
                    (mc.player!!.velocity as IVec3d).setY(0.0)
                }
            }

            Mode.CREATIVE -> {
                mc.player!!.abilities.flySpeed = speed
                mc.player!!.abilities.allowFlying = true

                if (!mc.player!!.abilities.creativeMode) {
                    mc.player!!.abilities.flying = true
                }
            }
        }
    }

    enum class Mode {
        MOTION,
        CREATIVE
    }

}