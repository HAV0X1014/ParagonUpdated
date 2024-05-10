package com.paragon.mixin.mixins.world.block;

import com.paragon.Paragon;
import com.paragon.backend.event.events.render.DrawSideOfBlockEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author aesthetical
 * @since 02/20/23
 */
@Mixin(Block.class)
public class MixinBlock {

    @Inject(method = "shouldDrawSide", at = @At("RETURN"), cancellable = true)
    private static void shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos otherPos, CallbackInfoReturnable<Boolean> info) {
        DrawSideOfBlockEvent event = new DrawSideOfBlockEvent(state.getBlock(), pos, state, info.getReturnValue());
        if (Paragon.bus.post(event)) {
            info.setReturnValue(event.getDrawSide());
        }
    }

}
