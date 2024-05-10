package com.paragon.client.modules.movement

import com.paragon.backend.event.events.move.MoveEvent
import com.paragon.backend.event.events.move.MoveUpdateEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.mixin.duck.IVec3d
import com.paragon.util.calculations.MoveUtil
import com.paragon.util.calculations.MoveUtil.moving
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import kotlin.math.sqrt

/**
 * @author aesthetical
 * @since 02/19/23
 */
object LongJump : Module("Long Jump", "jumps longer than ur mom to a mcdonalds", Category.MOVEMENT) {

    private var mode by enum("Mode", Mode.NCP, "How to long jump")
    private var boost by double("Boost", 1.5, 0.1, 0.1..8.0, "Boost speed for jumping")

    private var moveSpeed = 0.0
    private var travelDistance = 0.0
    private var stage = 0

    override val info = { mode.toString() }

    override fun enable() {
        super.enable()
        moveSpeed = 0.0
        travelDistance = 0.0
        stage = 0
    }

    @EventListener
    fun onMove(event: MoveEvent) {
        if (moving()) {

            if (mode == Mode.NCP) {
                when (stage) {
                    0 -> moveSpeed = boost + MoveUtil.getBaseNcpSpeed(0) - 0.05
                    1 -> {
                        event.setY(0.42)
                        (mc.player!!.velocity as IVec3d).setY(0.42)
                        moveSpeed *= 2.13
                    }
                    2 -> {
                        val diff: Double = 0.66 * (travelDistance - MoveUtil.getBaseNcpSpeed(0))
                        moveSpeed = travelDistance - diff
                    }
                    else -> moveSpeed = travelDistance - travelDistance / 159.0
                }

                moveSpeed = moveSpeed.coerceAtLeast(MoveUtil.getBaseNcpSpeed(0))
                event.setSpeed(moveSpeed)

                ++stage
            } else if (mode == Mode.VANILLA) {
                if (mc.player!!.isOnGround) {
                    event.setY(0.42)
                    (mc.player!!.velocity as IVec3d).setY(0.42)
                }

                event.setSpeed(boost)
            }
        }
    }

    @EventListener
    fun onMoveUpdate(event: MoveUpdateEvent?) {
        val diffX: Double = mc.player!!.getX() - mc.player!!.prevX
        val diffZ: Double = mc.player!!.getZ() - mc.player!!.prevZ
        travelDistance = sqrt(diffX * diffX + diffZ * diffZ)
    }

    enum class Mode {
        NCP, VANILLA
    }
}