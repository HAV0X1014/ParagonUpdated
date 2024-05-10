package com.paragon.mixin.mixins.net.packet.s2c;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author aesthetical
 * @since 02/18/23
 */
@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface IEntityVelocityUpdateS2CPacket {

    @Accessor("velocityX")
    @Final @Mutable
    void setVelocityX(int velocityX);

    @Accessor("velocityY")
    @Final @Mutable
    void setVelocityY(int velocityY);

    @Accessor("velocityZ")
    @Final @Mutable
    void setVelocityZ(int velocityZ);

}
