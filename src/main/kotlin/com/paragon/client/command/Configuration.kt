package com.paragon.client.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.paragon.Paragon
import com.paragon.backend.command.Command
import com.paragon.backend.command.argument.EnumArgumentType
import com.paragon.util.io.FileUtil
import com.paragon.util.mc
import com.paragon.util.print
import java.util.*

/**
 * @author aesthetical
 * @since 02/26/23
 */
object Configuration : Command(
        arrayOf("config", "configuration", "cfg", "preset"),
        usage = "[save | load | delete | list] <name>",
        description = "Manages configurations") {

    override fun run(ctx: LiteralArgumentBuilder<Any>) {
        ctx.then(RequiredArgumentBuilder.argument<Any?, Enum<*>?>("action", EnumArgumentType.enum(Action::class.java))
                .executes {

                    when (EnumArgumentType.enum<Action>(it, "action")) {
                        Action.LIST -> {
                            val fileList = FileUtil.PARAGON_PATH.resolve("configs").listFiles()
                            if (fileList == null) {
                                mc.inGameHud.print("There are no saved configurations")
                                return@executes SUCCESS
                            }

                            mc.inGameHud.print(StringBuilder().apply {

                                append("Config${if (fileList.size != 1) "s" else ""} list (${fileList.size})")
                                append(": ")

                                val joiner = StringJoiner(", ")
                                for (file in fileList) {
                                    joiner.add(file.nameWithoutExtension)
                                }

                                append(joiner)
                            }.toString())
                        }

                        else -> mc.inGameHud.print("Please provide a configuration name to load, save or delete")
                    }

                    return@executes SUCCESS
                }
                .then(RequiredArgumentBuilder.argument<Any?, String?>("configName", StringArgumentType.word())
                        .executes {

                            val configName = StringArgumentType.getString(it, "configName")
                            if (configName.isNullOrEmpty()) {
                                mc.inGameHud.print("Please provide a configuration name to load, save or delete")
                                return@executes SUCCESS
                            }

                            when (EnumArgumentType.enum<Action>(it, "action")) {
                                Action.LOAD -> mc.inGameHud.print(Paragon.moduleManager.load(configName))
                                Action.SAVE -> {
                                    Paragon.moduleManager.save(configName)
                                    mc.inGameHud.print("Saved config to file \"$configName.json\"")
                                }
                                Action.DELETE -> {

                                    if (configName.equals("current")) {
                                        mc.inGameHud.print("You cannot delete the current configuration")
                                        return@executes SUCCESS
                                    }

                                    mc.inGameHud.print(
                                            if (FileUtil.delete(FileUtil.PARAGON_PATH.resolve("configs").resolve("$configName.json")))
                                                "Deleted config file \"$configName.json\" successfully."
                                            else "Unable to delete config file \"$configName.json\". Make sure it exists")
                                }
                                else -> mc.inGameHud.print("Cannot list a config (yet)")
                            }
                            return@executes SUCCESS
                        }))
    }

    enum class Action {
        SAVE, LOAD, DELETE, LIST
    }
}