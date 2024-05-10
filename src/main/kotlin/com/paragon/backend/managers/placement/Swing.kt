package com.paragon.backend.managers.placement

import com.paragon.util.mc
import com.paragon.util.player.PlayerUtil
import net.minecraft.util.Hand

/**
 * @author surge
 * @since 09/03/2023
 */
enum class Swing(val swing: (Hand) -> Unit) {

    /**
     * Swing client side
     */
    CLIENT({ mc.player!!.swingHand(it) }),

    /**
     * Swing server side
     */
    SERVER({ PlayerUtil.silentSwing(it) }),

    /**
     * Don't swing
     */
    NONE({})

}