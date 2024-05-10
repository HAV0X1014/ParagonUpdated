package com.paragon.mixin.mixins.render.gui;

import com.paragon.Paragon;
import com.paragon.backend.event.events.render.RenderHUDEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author aesthetical
 * @since 02/17/23
 */
@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Shadow @Final private MinecraftClient client;
    @Shadow private int scaledWidth;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",
                    ordinal = 4,
                    shift = Shift.BEFORE))
    public void hookRender(MatrixStack stack, float deltaTick, CallbackInfo info) {
        Window window = client.getWindow();
        Paragon.bus.post(new RenderHUDEvent(stack, deltaTick, window.getWidth(), window.getHeight()));
    }

    @ModifyArg(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
                    ordinal = 1),
            index = 1)
    public int hookModifyRenderHotbar$selectedSlot(int selectedSlotIn) {
        int slot = Paragon.inventoryManager.isDesynced() ? Paragon.inventoryManager.getSlot() : client.player.getInventory().selectedSlot;
        int i = this.scaledWidth / 2;
        return i - 91 - 1 + slot * 20;
    }

}
