package com.paragon.mixin.mixins.render;

import com.paragon.Paragon;
import com.paragon.backend.event.events.render.GammaModifyEvent;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author aesthetical
 * @since 02/18/23
 */
@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager {

    // this is the only place getGamma() is used, so this has to be it?
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
    public float redirectUpdate$floatValue(Double instance) {
        GammaModifyEvent event = new GammaModifyEvent(instance.floatValue());
        return Paragon.bus.post(event) ? event.gamma : instance.floatValue();
    }

}
