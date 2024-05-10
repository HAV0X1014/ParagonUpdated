package com.paragon.mixin.mixins.net.packet.c2s;

import com.paragon.mixin.duck.IPlayerInteractEntityC2SPacket;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.paragon.util.MiscKt.getMc;

@Mixin(PlayerInteractEntityC2SPacket.class)
public class MixinPlayerInteractEntityC2SPacket implements IPlayerInteractEntityC2SPacket {

    @Shadow @Final private int entityId;

    @Shadow @Final private PlayerInteractEntityC2SPacket.InteractTypeHandler type;

    @Override
    public PlayerInteractEntityC2SPacket.InteractType getType() {
        return type.getType();
    }

    @Override
    public Entity getEntity() {
        return getMc().world.getEntityById(entityId);
    }

}
