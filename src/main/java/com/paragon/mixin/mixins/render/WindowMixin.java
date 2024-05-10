package com.paragon.mixin.mixins.render;

import com.paragon.Paragon;
import com.paragon.util.calculations.Timer;
import com.paragon.util.rendering.NVGWrapper;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author surge
 * @since 11/02/2023
 */
@Mixin(Window.class)
public class WindowMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void hookInit(WindowEventHandler eventHandler, MonitorTracker monitorTracker, WindowSettings settings, String videoMode, String title, CallbackInfo info) {
        Paragon.getLogger().info("Initialising NanoVG");
        Timer timer = new Timer();
        timer.reset();
        NVGWrapper.initialise();
        Paragon.getLogger().info("Initialised NanoVG in {}ms", timer.timeMs());
    }

}
