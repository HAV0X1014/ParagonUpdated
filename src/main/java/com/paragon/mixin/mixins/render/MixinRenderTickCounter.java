package com.paragon.mixin.mixins.render;

import com.paragon.mixin.duck.IRenderTickCounter;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author aesthetical
 * @since 02/17/23
 */
@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter implements IRenderTickCounter {

    @Shadow @Final @Mutable
    private float tickTime;

    @Override
    public void setTickLength(float tickLength) {
        tickTime = tickLength;
    }

    @Override
    public float getTickLength() {
        return tickTime;
    }

}
