package com.paragon.mixin.mixins.input;

import com.paragon.Paragon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemUsageContext.class)
public class MixinItemUsageContext {

    @Inject(method = "getStack", at = @At("RETURN"), cancellable = true)
    public void hookGetStack(CallbackInfoReturnable<ItemStack> info) {
        if (MinecraftClient.getInstance().player != null && info.getReturnValue().equals(MinecraftClient.getInstance().player.getMainHandStack()) && Paragon.inventoryManager.isDesynced()) {
            info.setReturnValue(Paragon.inventoryManager.getServerStack());
        }
    }

}

