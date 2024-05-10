package com.paragon.client.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.paragon.backend.command.Command
import com.paragon.backend.command.argument.ModuleArgumentType
import com.paragon.backend.module.Module
import com.paragon.util.mc
import com.paragon.util.print

/**
 * @author aesthetical
 * @since 02/24/23
 */
object Drawn : Command(arrayOf("drawn", "hide", "hid"), usage = "[module]") {
    override fun run(ctx: LiteralArgumentBuilder<Any>) {
        ctx.then(RequiredArgumentBuilder.argument<Any?, Module?>("module", ModuleArgumentType.module())
                .executes {
                    val module = ModuleArgumentType.module(it, "module")
                    module.visible = !module.visible
                    mc.inGameHud.print("Module is now ${if (module.visible) "shown" else "hidden"} on the arraylist")
                    return@executes SUCCESS
                })
    }
}