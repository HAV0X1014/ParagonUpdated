package com.paragon.mixin.duck;

import net.minecraft.network.Packet;

/**
 * @author aesthetical
 * @since 02/18/23
 */
public interface IClientConnection {
    void sendPacketNoEvent(Packet<?> packet);
}
