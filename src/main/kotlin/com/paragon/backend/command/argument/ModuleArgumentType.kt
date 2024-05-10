package com.paragon.backend.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.paragon.Paragon
import com.paragon.backend.module.Module

/**
 * @author aesthetical
 * @since 02/24/23
 */
class ModuleArgumentType : ArgumentType<Module>, CommandExceptionType {
    override fun parse(reader: StringReader): Module {
        val s = reader.readString()
        val parsed = s.lowercase().replace(" ", "")
        return Paragon.moduleManager.getModules {
            it.name.lowercase().replace(" ", "").equals(parsed, ignoreCase = true)
        }.firstOrNull() ?: throw CommandSyntaxException(this) { "\"$s\" is a non-existent (null) module." }
    }

    companion object {

        @JvmStatic
        fun module(ctx: CommandContext<Any>, name: String): Module {
            return ctx.getArgument(name, Module::class.java)
        }

        @JvmStatic
        fun module(): ModuleArgumentType {
            return ModuleArgumentType()
        }
    }
}