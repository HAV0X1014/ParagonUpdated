package com.paragon.mixin.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.paragon.Paragon;
import com.paragon.backend.event.events.entity.EntityTraceEvent;
import com.paragon.backend.event.events.entity.RaycastEvent;
import com.paragon.backend.event.events.render.GameRenderEvent;
import com.paragon.backend.event.events.render.PreGameRenderEvent;
import com.paragon.backend.framebuffer.MultiSampledFramebuffer;
import com.paragon.mixin.duck.IGameRenderer;
import com.paragon.util.rendering.Renderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * @author surge
 * @since 12/02/2023
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements IGameRenderer {

    @Shadow protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Shadow public abstract MinecraftClient getClient();

    @Inject(method = "renderWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"))
    public void hookRenderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        RenderSystem.backupProjectionMatrix();
        Paragon.bus.post(new GameRenderEvent(matrices, tickDelta));
        MultiSampledFramebuffer.use(Renderer::drawAndClear);
        RenderSystem.restoreProjectionMatrix();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void hookRenderHead(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        PreGameRenderEvent event = new PreGameRenderEvent();
        Paragon.bus.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @ModifyArgs(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    public void hookUpdateTargetedEntityRaycast(Args args) {
        RaycastEvent event = new RaycastEvent(args.get(0), args.get(1), args.get(2));
        Paragon.bus.post(event);

        args.setAll(
            event.getDistance(),
            event.getTickDelta(),
            event.getIncludeFluids()
        );
    }

    @Inject(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"), cancellable = true)
    public void hookUpdateTargetedEntity(float tickDelta, CallbackInfo ci) {
        EntityTraceEvent event = new EntityTraceEvent(getClient().crosshairTarget);
        Paragon.bus.post(event);

        if (event.isCancelled()) {
            getClient().getProfiler().pop();
            ci.cancel();
        }
    }

    @Override
    public void hookBobView(MatrixStack matrices, float tickDelta) {
        bobView(matrices, tickDelta);
    }

}
