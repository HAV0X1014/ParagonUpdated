package com.paragon.mixin.mixins.render;

import com.paragon.Paragon;
import com.paragon.backend.event.events.render.ClipToSpaceEvent;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author aesthetical
 * @since 02/19/23
 */
@Mixin(Camera.class)
public class MixinCamera {

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    public void hookClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> info) {
        ClipToSpaceEvent event = new ClipToSpaceEvent(desiredCameraDistance);
        if (Paragon.bus.post(event)) {
            info.setReturnValue(event.getDistance());
        }
    }

}
