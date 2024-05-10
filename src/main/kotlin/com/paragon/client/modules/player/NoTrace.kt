package com.paragon.client.modules.player

import com.paragon.Paragon
import com.paragon.backend.bind.Bind
import com.paragon.backend.event.events.entity.EntityTraceEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.item.PickaxeItem

/**
 * @author surge
 * @since 28/02/2023
 */
object NoTrace : Module("No Trace", "Allows you to interact through entities", Category.PLAYER) {

    private val pickaxe by bool("Pickaxe", true, "Apply when holding a pickaxe")
    private val bind by bind("Force Bind", Bind(this))

    init {
        Paragon.keyboardManager.addBind(bind)
    }

    @EventListener
    fun onEntityTrace(event: EntityTraceEvent) {
        if (pickaxe && mc.player!!.isHolding { it.item is PickaxeItem } || bind.state) {
            event.cancel()
        }
    }

}