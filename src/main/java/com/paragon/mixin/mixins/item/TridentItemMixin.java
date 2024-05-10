package com.paragon.mixin.mixins.item;

import com.paragon.Paragon;
import com.paragon.backend.event.events.move.TridentVelocityEvent;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * @author surge
 * @since 24/02/2023
 */
@Mixin(TridentItem.class)
public class TridentItemMixin {

    @ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"))
    private void hookOnStoppedUsing(Args args) {
        TridentVelocityEvent event = new TridentVelocityEvent(args.get(0), args.get(1), args.get(2));
        Paragon.bus.post(event);

        args.setAll(event.getX(), event.getY(), event.getZ());
    }

}
