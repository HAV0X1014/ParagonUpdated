package com.paragon.mixin.duck;

import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;

/**
 * @author aesthetical
 * @since 02/18/23
 */
public interface IClientPlayerInteractionManager {
    void hookSendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);
}
