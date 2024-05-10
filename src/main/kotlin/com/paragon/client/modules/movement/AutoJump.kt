package com.paragon.client.modules.movement

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener

/**
 * @author surge
 * @since 06/03/2023
 */
object AutoJump : Module("Auto Jump", "Automatically jumps for you", Category.MOVEMENT) {

    private val mode by enum("Mode", Mode.SPRINTING, "When to jump")

    @EventListener
    fun onTick(event: TickEvent) {
        if (mc.player!!.isOnGround && mode.check() && !mc.player!!.isSneaking) {
            mc.player!!.jump()
        }
    }

    enum class Mode(val check: () -> Boolean) {
        CONSTANT({ true }),
        MOVING({ mc.player!!.lastRenderX != mc.player!!.x || mc.player!!.lastRenderZ != mc.player!!.z }),
        SPRINTING({ (mc.player!!.forwardSpeed != 0f || mc.player!!.sidewaysSpeed != 0f) && mc.player!!.isSprinting }),
        UNDER_BLOCK_SPRINT({ SPRINTING.check() && !mc.world!!.getBlockState(mc.player!!.blockPos.up(2)).isReplaceable })
    }

}