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
object Toggle : Command(arrayOf("toggle", "t"), usage = "[module name]", description = "Toggles a module on or off") {
    override fun run(ctx: LiteralArgumentBuilder<Any>) {
        ctx.then(RequiredArgumentBuilder.argument<Any?, Module?>("moduleName", ModuleArgumentType.module())
                .executes {
                    val module = ModuleArgumentType.module(it, "moduleName")
                    module.toggle()

                    mc.inGameHud.print("Toggled ${module.name} ${if (module.isEnabled) "on" else "off"}.")
                    return@executes SUCCESS
                })
    }
}