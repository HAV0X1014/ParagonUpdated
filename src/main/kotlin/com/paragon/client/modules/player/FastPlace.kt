package com.paragon.client.modules.player

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.mixin.duck.IMinecraftClient
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.Hand

/**
 * @author aesthetical
 * @since 02/18/23
 */
object FastPlace : Module("Fast Place", "Places items fast", Category.PLAYER) {

    private val speed by int("Speed", 4, 1, 1..4, "How fast to place items at")
    private val exp by bool("EXP", true, "If to place exp fast")
    private val blocks by bool("Blocks", true, "If to place blocks fast")
    private val fireworks by bool("Fireworks", true, "If to place fireworks fast")
    private val crystals by bool("Crystals", true, "If to place end crystals fast")

    @EventListener
    fun onTick(event: TickEvent?) {

        // 10/10 code i love this
        if (shouldFastPlace(mc.player!!.getStackInHand(Hand.MAIN_HAND)) || shouldFastPlace(mc.player!!.getStackInHand(Hand.OFF_HAND))) {
            (mc as IMinecraftClient).setItemUseCooldown(4 - speed)
        }
    }

    private fun shouldFastPlace(activeStack: ItemStack): Boolean {
        if (!exp && activeStack.item == Items.EXPERIENCE_BOTTLE) {
            return false
        }

        if (!blocks && activeStack.item is BlockItem) {
            return false
        }

        if (!fireworks && activeStack.item == Items.FIREWORK_ROCKET) {
            return false
        }

        return !(!crystals && activeStack.item == Items.END_CRYSTAL)
    }

}