package com.paragon.client.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.paragon.Paragon
import com.paragon.backend.command.Command
import com.paragon.util.mc
import com.paragon.util.print

/**
 * @author aesthetical
 * @since 02/24/23
 */
object Prefix : Command(arrayOf("prefix", "pfx", "setprefix"), description = "Sets the command prefix") {
    override fun run(ctx: LiteralArgumentBuilder<Any>) {
        ctx.then(RequiredArgumentBuilder.argument<Any?, String?>("prefix", StringArgumentType.word())
                .executes {
                    Paragon.commandManager.prefix = StringArgumentType.getString(it, "prefix")
                    mc.inGameHud.print("Set the new command prefix to \"${Paragon.commandManager.prefix}\"")
                    return@executes SUCCESS
                })
                .executes {
                    mc.inGameHud.print("Your current prefix is \"${Paragon.commandManager.prefix}\" (obviously dumbass)")
                    return@executes SUCCESS
                }
    }
}