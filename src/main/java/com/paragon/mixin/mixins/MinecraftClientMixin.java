package com.paragon.mixin.mixins;

import com.paragon.Paragon;
import com.paragon.backend.event.events.mc.ShutdownEvent;
import com.paragon.backend.event.events.mc.TickEvent;
import com.paragon.backend.event.events.mc.TitleEvent;
import com.paragon.backend.event.events.render.FPSLimitEvent;
import com.paragon.backend.event.events.render.SetScreenEvent;
import com.paragon.mixin.duck.IMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Session;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author surge, aesthetical
 * @since 11/02/2023
 */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements IMinecraftClient {

    @Shadow public ClientWorld world;
    @Shadow public ClientPlayerEntity player;
    @Shadow public Screen currentScreen;

    @Shadow private int itemUseCooldown;

    @Shadow @Final
    private RenderTickCounter renderTickCounter;

    @Shadow @Final @Mutable
    private Session session;

    @Inject(method = "tick", at = @At("TAIL"))
    public void hookOnTick(CallbackInfo ci) {
        if (world != null && player != null) {
            Paragon.bus.post(new TickEvent());
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    public void hookClose(CallbackInfo ci) {
        Paragon.bus.post(new ShutdownEvent());
    }

    @Inject(method = "getWindowTitle", at = @At("RETURN"), cancellable = true)
    public void hookGetWindowTitle(CallbackInfoReturnable<String> info) {
        TitleEvent event = new TitleEvent(info.getReturnValue());
        Paragon.bus.post(event);
        info.setReturnValue(event.title);
    }

    @Inject(method = "setScreen", at = @At("HEAD"))
    public void hookSetScreen(Screen screen, CallbackInfo info) {
        Paragon.bus.post(new SetScreenEvent(currentScreen, screen));
    }

    @Inject(method = "getFramerateLimit", at = @At("HEAD"), cancellable = true)
    public void hookGetFramerateLimit(CallbackInfoReturnable<Integer> cir) {
        FPSLimitEvent event = new FPSLimitEvent();
        Paragon.bus.post(event);

        if (event.isCancelled()) {
            cir.cancel();
            cir.setReturnValue(event.getLimit());
        }
    }

    @Override
    public RenderTickCounter getRenderTickCounter() {
        return renderTickCounter;
    }

    @Override
    public void setItemUseCooldown(int itemUseCooldown) {
        this.itemUseCooldown = itemUseCooldown;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

}
