package com.paragon.backend.command

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.builder.LiteralArgumentBuilder

/**
 * @author aesthetical
 * @since 02/24/23
 */
abstract class Command(val aliases: Array<String>, val description: String = "No command description provided", val usage: String = "") {

    abstract fun run(ctx: LiteralArgumentBuilder<Any>)

    companion object {
        const val SUCCESS = SINGLE_SUCCESS
        const val INVALID_USAGE = SINGLE_SUCCESS + 1
    }
}