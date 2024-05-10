package com.paragon.client.modules.movement

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.event.events.move.MoveEvent
import com.paragon.backend.event.events.move.MoveUpdateEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.client.modules.player.GameSpeed.setTimerSpeed
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
object Speed : Module("Speed", "Speedy speed", Category.MOVEMENT) {

    private var mode by enum("Mode", Mode.OLD_NCP_HOP, "How to go vroom vroom")
    private var useTimer by bool("Timer", true, "Use timer to give an extra speed boost")

    private var moveSpeed = 0.0
    private var travelDistance = 0.0
    private var boost = false
    private var hopStage = 0

    override val info = { mode.name }

    override fun enable() {
        super.enable()
        hopStage = 4
        moveSpeed = 0.0
        travelDistance = 0.0
        boost = false
    }

    @EventListener
    fun onTick(event: TickEvent) {
        if (mode == Mode.OLD_NCP_YPORT) {
            if (moving()) {
                mc.player!!.isSprinting = true
                moveSpeed = 1.25 * MoveUtil.getBaseNcpSpeed(0) - 0.1
                if (mc.player!!.isOnGround) {
                    mc.player!!.jump()
                    moveSpeed *= if (useTimer) {
                        if (mc.player!!.age % 10 == 0) {
                            setTimerSpeed(1.35f)
                        } else {
                            setTimerSpeed(if (boost) 1.088f else 1.098f)
                        }
                        if (boost) 1.62 else 1.526
                    } else {
                        setTimerSpeed(1.0f)
                        if (boost) 1.622 else 1.545
                    }
                } else {
                    boost = !boost
                    (mc.player!!.velocity as IVec3d).setY(-4.0)
                    setTimerSpeed(1.0f)
                }
            }
        }
    }

    @EventListener
    fun onMove(event: MoveEvent) {
        when (mode) {
            Mode.OLD_NCP_YPORT -> {
                if (moving()) {
                    event.setSpeed(moveSpeed)
                } else {
                    event.nullOutVelocity()
                }
            }

            Mode.OLD_NCP_HOP, Mode.NEW_NCP_HOP -> {
                val oldNcp: Boolean = mode == Mode.OLD_NCP_HOP
                if (mc.player!!.isOnGround && moving()) {
                    hopStage = 2
                }

                if (hopStage == 1) {
                    val factor = if (oldNcp) 1.37 else 1.6
                    moveSpeed = factor * MoveUtil.getBaseNcpSpeed(15) - 0.01
                    hopStage = 2
                } else if (hopStage == 2) {
                    if (mc.player!!.isOnGround && moving()) {
                        val height = MoveUtil.getJumpHeight(0.3995)
                        event.setY(height)
                        (mc.player!!.velocity as IVec3d).setY(height)

                        moveSpeed *= if (oldNcp) {
                            if (boost) 1.624 else 1.543
                        } else {
                            if (boost) 1.53 else 1.41
                        }

                        setTimerSpeed(1.0f)
                    }

                    hopStage = 3
                } else if (hopStage == 3) {
                    if (useTimer) {
                        if (oldNcp) {
                            setTimerSpeed(1.088f)
                        } else {
                            setTimerSpeed(if (boost) 1.088f else 1.073f)
                        }
                    } else {
                        setTimerSpeed(1.0f)
                    }

                    val adjustment: Double = (if (boost) 0.72 else 0.66) * (travelDistance - MoveUtil.getBaseNcpSpeed(15))
                    moveSpeed = travelDistance - adjustment
                    boost = !boost
                    hopStage = 4
                } else if (hopStage == 4) {
                    if (mc.world!!.getEntityCollisions(mc.player, mc.player!!.boundingBox.offset(0.0, mc.player!!.velocity.y, 0.0)).isNotEmpty()) {
                        hopStage = 1
                    }

                    moveSpeed = travelDistance - travelDistance / 150.0
                }

                moveSpeed = Math.max(moveSpeed, MoveUtil.getBaseNcpSpeed(15))
                if (moving()) {
                    event.setSpeed(moveSpeed)
                } else {
                    event.nullOutVelocity()
                }
            }

            else -> {
                // empty, kys kotlin
            }
        }
    }

    @EventListener
    fun onMoveUpdate(event: MoveUpdateEvent?) {
        val diffX = mc.player!!.x - mc.player!!.prevX
        val diffZ = mc.player!!.z - mc.player!!.prevZ
        travelDistance = sqrt(diffX * diffX + diffZ * diffZ)
    }

    enum class Mode {
        OLD_NCP_HOP, OLD_NCP_YPORT, NEW_NCP_HOP
    }

}