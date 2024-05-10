package com.paragon.mixin.duck;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

/**
 * @author aesthetical
 * @since 02/17/23
 */
public interface IPlayerInteractEntityC2SPacket {
    PlayerInteractEntityC2SPacket.InteractType getType();

    Entity getEntity();
}
