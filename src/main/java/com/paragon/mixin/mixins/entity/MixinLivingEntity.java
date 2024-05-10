package com.paragon.mixin.mixins.entity;

import com.paragon.Paragon;
import com.paragon.backend.event.EventEra;
import com.paragon.backend.event.events.entity.EntityTickEvent;
import com.paragon.mixin.duck.ILivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author aesthetical
 * @since 02/19/23
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements ILivingEntity {
    @Shadow protected int lastAttackedTicks;

    private float[] renderRotations;
    private float[] prevRenderRotations;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void hookInit(CallbackInfo info) {
        renderRotations = new float[2];
        prevRenderRotations = new float[2];

        renderRotations[0] = getYaw();
        renderRotations[1] = getPitch();
        prevRenderRotations[0] = getYaw();
        prevRenderRotations[1] = getPitch();
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void hookBaseTickPre(CallbackInfo info) {
        Paragon.bus.post(new EntityTickEvent(EventEra.PRE, (LivingEntity) (Object) this));
    }

    @Inject(method = "baseTick", at = @At("TAIL"))
    public void hookBaseTickPost(CallbackInfo info) {
        Paragon.bus.post(new EntityTickEvent(EventEra.POST, (LivingEntity) (Object) this));

        prevRenderRotations[0] = renderRotations[0];
        prevRenderRotations[1] = renderRotations[1];
    }

    @Override
    public int getLastAttackedTicks() {
        return lastAttackedTicks;
    }

    @Override
    public float[] getRenderRotations() {
        return renderRotations;
    }

    @Override
    public float[] getPreviousRenderRotations() {
        return prevRenderRotations;
    }
}
