package com.paragon.backend.managers

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.paragon.Paragon
import com.paragon.backend.command.Command
import com.paragon.backend.config.Config
import com.paragon.backend.event.events.net.PacketEvent
import com.paragon.client.command.Configuration
import com.paragon.client.command.Drawn
import com.paragon.client.command.Prefix
import com.paragon.client.command.Toggle
import com.paragon.util.io.FileUtil
import com.paragon.util.mc
import com.paragon.util.print
import me.bush.eventbus.annotation.EventListener
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket


/**
 * @author aesthetical
 * @since 02/24/23
 */
class CommandManager {

    private val dispatcher = CommandDispatcher<Any>()
    val commandMap = mutableMapOf<String, Command>()

    var prefix = "."

    init {
        Paragon.bus.subscribe(this)

        dispatcher.setConsumer { context, _, result ->
            val command = commandMap[context.input.split(" ")[0]]
            if (command == null) {
                mc.inGameHud.print("Unknown command execution. Run ${prefix}help")
                return@setConsumer
            }

            when (result) {
                Command.INVALID_USAGE -> mc.inGameHud.print("Incorrect usage. Usage: ${command.usage}")
            }
        }

        object : Config("command_prefix.txt") {
            override fun save() {
                FileUtil.write(file, prefix)
            }

            override fun load() {
                val content = FileUtil.read(file)
                if (content.isNullOrEmpty()) {
                    return
                }

                prefix = content.trim().replace(" ", "").replace("\n", "")
            }

        }

        register(Configuration, Drawn, Prefix, Toggle)

        // TODO: command autocomplete in chat gui
    }

    @EventListener
    fun onPacketOutbound(event: PacketEvent.Outbound) {
        if (event.packet is ChatMessageC2SPacket) {
            val packet = event.packet
            if (packet.chatMessage.startsWith(prefix)) {

                event.isCancelled = true

                try {
                    Paragon.logger.info("Dispatching command with input ${packet.chatMessage}")
                    dispatcher.execute(dispatcher.parse(packet.chatMessage.substring(prefix.length), Command.SUCCESS))
                } catch (e: CommandSyntaxException) {
                    mc.inGameHud.print(e.rawMessage.string)
                }

            }
        }
    }

    private fun register(vararg commands: Command) {

        commands.forEach { command ->
            command.aliases.forEach {
                commandMap[it] = command

                val argumentBuilder = LiteralArgumentBuilder.literal<Any>(it)
                        .executes { return@executes Command.INVALID_USAGE }

                command.run(argumentBuilder)
                dispatcher.register(argumentBuilder)
            }
        }

    }
}