package com.paragon.mixin.mixins.world.block;

import com.paragon.Paragon;
import com.paragon.backend.event.events.render.BlockAmbientLightLevelEvent;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author aesthetical
 * @since 02/20/23
 */
@Mixin(AbstractBlockState.class)
public class MixinAbstractBlockState {
    @Inject(method = "getAmbientOcclusionLightLevel", at = @At("RETURN"), cancellable = true)
    public void hookGetAmbientOcclusionLightLevel(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info) {
        BlockAmbientLightLevelEvent event = new BlockAmbientLightLevelEvent(pos, info.getReturnValue());
        if (Paragon.bus.post(event)) {
            info.setReturnValue(event.getLightLevel());
        }
    }
}
