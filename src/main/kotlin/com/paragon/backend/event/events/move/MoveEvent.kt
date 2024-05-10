package com.paragon.backend.event.events.move

import com.paragon.mixin.duck.IVec3d
import com.paragon.util.calculations.MoveUtil.strafe
import me.bush.eventbus.event.Event
import net.minecraft.util.math.Vec3d

/**
 * @author aesthetical
 * @since 02/18/23
 */
class MoveEvent(val motionVec: Vec3d) : Event() {

    fun setX(x: Double) {
        (motionVec as IVec3d).setX(x)
    }

    fun setY(y: Double) {
        (motionVec as IVec3d).setY(y)
    }

    fun setZ(z: Double) {
        (motionVec as IVec3d).setZ(z)
    }

    fun nullOutVelocity() {
        setX(0.0)
        setZ(0.0)
    }

    fun setSpeed(speed: Double) {
        val strafe = strafe(speed)
        setX(strafe[0])
        setZ(strafe[1])
    }

    override fun isCancellable(): Boolean {
        return false
    }

}