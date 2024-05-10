package com.paragon.mixin.mixins.net.packet.c2s;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author aesthetical
 * @since 02/18/23
 */
@Mixin(PlayerMoveC2SPacket.class)
public interface IPlayerMoveC2SPacket {

    @Accessor("onGround") @Final @Mutable
    void setOnGround(boolean onGround);

}
