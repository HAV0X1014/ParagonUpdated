package com.paragon.mixin.mixins.render.entity;

import com.paragon.mixin.duck.ILivingEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author aesthetical
 * @since 02/17/23
 */

/**
 * TODO: Make these smooth (rotations, I assume??)
 */
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
        extends EntityRenderer<T>
        implements FeatureRendererContext<T, M> {

    private float headYaw, prevHeadYaw;
    private float pitch, prevPitch;
    private boolean modified = false;

    protected MixinLivingEntityRenderer(Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void hookRenderPre(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        if (livingEntity != null && livingEntity.equals(MinecraftClient.getInstance().player)) {

            modified = true;

            headYaw = livingEntity.headYaw;
            prevHeadYaw = livingEntity.prevHeadYaw;

            pitch = livingEntity.getPitch();
            prevPitch = livingEntity.prevPitch;

            float[] rots = ((ILivingEntity) livingEntity).getRenderRotations();
            float[] prevRots = ((ILivingEntity) livingEntity).getPreviousRenderRotations();

            livingEntity.headYaw = rots[0];
            livingEntity.prevHeadYaw = prevRots[0];

            livingEntity.setPitch(rots[1]);
            livingEntity.prevPitch = prevRots[1];
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    public void hookRenderPost(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        if (livingEntity != null && livingEntity.equals(MinecraftClient.getInstance().player) && modified) {
            modified = false;

            livingEntity.headYaw = headYaw;
            livingEntity.prevHeadYaw = prevHeadYaw;

            livingEntity.setPitch(pitch);
            livingEntity.prevPitch = prevPitch;
        }
    }
}
