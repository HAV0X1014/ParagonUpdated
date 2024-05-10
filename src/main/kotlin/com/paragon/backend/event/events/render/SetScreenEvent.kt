package com.paragon.backend.event.events.render

import me.bush.eventbus.event.Event
import net.minecraft.client.gui.screen.Screen

/**
 * @author aesthetical
 * @since 02/18/23
 */
class SetScreenEvent(val current: Screen?, val input: Screen?) : Event() {

    override fun isCancellable(): Boolean {
        return false
    }

}