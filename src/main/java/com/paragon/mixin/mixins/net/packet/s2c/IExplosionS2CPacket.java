package com.paragon.mixin.mixins.net.packet.s2c;

import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author aesthetical
 * @since 02/18/23
 */
@Mixin(ExplosionS2CPacket.class)
public interface IExplosionS2CPacket {

    @Accessor("playerVelocityX")
    @Final
    @Mutable
    void setVelocityX(float velocityX);

    @Accessor("playerVelocityY")
    @Final @Mutable
    void setVelocityY(float velocityY);

    @Accessor("playerVelocityZ")
    @Final @Mutable
    void setVelocityZ(float velocityZ);

}
