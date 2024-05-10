package com.paragon.mixin.mixins;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashTextResourceSupplier.class)
public class MixinSplashTextResourceSupplier {

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public void hookGet(CallbackInfoReturnable<String> info) {

    }
}
