package com.paragon.backend.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandExceptionType
import com.mojang.brigadier.exceptions.CommandSyntaxException

/**
 * @author aesthetical
 * @since 02/26/23
 */
class EnumArgumentType<T : Enum<*>>(private val enum: Class<T>) : ArgumentType<Enum<*>>, CommandExceptionType {
    override fun parse(reader: StringReader): T {
        val input = reader.readString()
        if (input.isNullOrEmpty()) {
            throw CommandSyntaxException(this) { "Cannot be null or empty" }
        }

        for (constant in enum.enumConstants) {
            if (input.equals(constant.toString(), ignoreCase = true)) {
                return constant
            }
        }

        throw CommandSyntaxException(this) { "\"$input\" is not a valid enum type" }
    }

    companion object {

        @JvmStatic
        inline fun <reified T : Enum<*>> enum(ctx: CommandContext<Any>, name: String): T {
            return ctx.getArgument(name, Enum::class.java) as T
        }

        @JvmStatic
        inline fun <reified T : Enum<*>> enum(enum: Class<T>): EnumArgumentType<T> {
            return EnumArgumentType(enum)
        }
    }
}