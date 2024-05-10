package com.paragon.client.modules.player

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import com.paragon.util.nullCheck
import me.bush.eventbus.annotation.EventListener

/**
 * @author aesthetical, finnish kid
 * @since 02/22/23
 */
object RotationLock : Module("Rotation Lock", "Locks your rotations", Category.PLAYER) {

    private var yaw = 0f
    private var pitch = 0f

    override fun enable() {
        if (nullCheck()) {
            return
        }

        yaw = mc.player!!.yaw
        pitch = mc.player!!.pitch
    }

    @EventListener
    fun onTick(event: TickEvent?) {
        if (nullCheck()) {
            return
        }

        mc.player!!.setHeadYaw(yaw)
        mc.player!!.setBodyYaw(yaw)
        mc.player!!.yaw = yaw
        mc.player!!.pitch = pitch
    }
}