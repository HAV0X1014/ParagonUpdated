package com.paragon.mixin.mixins.input.io;

import com.paragon.Paragon;
import com.paragon.backend.event.events.input.io.MouseEvent;
import com.paragon.mixin.duck.IMouse;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author aesthetical
 * @since 02/17/23
 */
@Mixin(Mouse.class)
public class MouseMixin implements IMouse {

    @Shadow private double eventDeltaWheel;

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void hookOnMouseButton(long window, int button, int action, int mods, CallbackInfo info) {
        Paragon.bus.post(new MouseEvent(button, action));
    }

    @Override
    public double getScrollDelta() {
        return eventDeltaWheel;
    }

}
