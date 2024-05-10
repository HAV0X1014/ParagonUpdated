package com.paragon.backend.event.events.net

import me.bush.eventbus.event.Event
import net.minecraft.network.Packet

/**
 * @author aesthetical
 * @since 02/17/23
 */
open class PacketEvent(val packet: Packet<*>) : Event() {

    override fun isCancellable(): Boolean {
        return true
    }

    class Inbound(packet: Packet<*>) : PacketEvent(packet)
    class Outbound(packet: Packet<*>) : PacketEvent(packet)

}