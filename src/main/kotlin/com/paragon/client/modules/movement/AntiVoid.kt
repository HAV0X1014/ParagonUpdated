package com.paragon.client.modules.movement

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.mixin.duck.IVec3d
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround

/**
 * @author aesthetical
 * @since 02/18/23
 */
object AntiVoid : Module("Anti Void", "Stops you from falling into the void", Category.MOVEMENT) {

    private val mode by enum("Mode", Mode.LAGBACK, "How to get you out of the void")

    override val info = { mode.toString() }

    @EventListener
    fun onTick(event: TickEvent?) {
        if (mc.player!!.y < mc.world!!.bottomY) {
            when (mode) {
                Mode.FLOAT -> (mc.player!!.velocity as IVec3d).setY(0.42)
                Mode.LAGBACK -> mc.player!!.networkHandler.sendPacket(PositionAndOnGround(mc.player!!.x, mc.player!!.y + 0.1, mc.player!!.z, false))
                Mode.SUSPEND -> (mc.player!!.velocity as IVec3d).setY(0.0)
            }
        }
    }

    enum class Mode {
        SUSPEND,
        FLOAT,
        LAGBACK
    }

}