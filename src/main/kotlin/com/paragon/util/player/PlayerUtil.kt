package com.paragon.util.player

import com.paragon.util.mc
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket
import net.minecraft.util.Hand

/**
 * @author aesthetical
 * @since 02/20/23
 */
object PlayerUtil {
    fun silentSwing(hand: Hand) {
        mc.player!!.networkHandler.sendPacket(HandSwingC2SPacket(hand))
        mc.player!!.swingHand(Hand.MAIN_HAND, false)
    }
}