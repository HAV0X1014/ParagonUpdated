package com.paragon.mixin.mixins.input;

import com.paragon.Paragon;
import com.paragon.backend.event.events.input.control.AttackBlockEvent;
import com.paragon.mixin.duck.IClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author aesthetical
 * @since 02/18/23
 */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {

    @Shadow
    protected abstract void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);

    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    public void hookAttackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
        if (Paragon.bus.post(new AttackBlockEvent(pos, direction))) {
            info.setReturnValue(false);
        }
    }

    // these below two redirects is for silent swap

    @Redirect(
            method = "interactBlockInternal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack hookRedirectInteractBlockInternal$getStackInHand(ClientPlayerEntity instance, Hand hand) {
        if (hand.equals(Hand.OFF_HAND)) {
            return instance.getStackInHand(hand);
        }

        return Paragon.inventoryManager.isDesynced() ? Paragon.inventoryManager.getServerStack() : instance.getStackInHand(Hand.MAIN_HAND);
    }

    @Redirect(
            method = "interactBlockInternal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
                    ordinal = 0))
    private boolean hookRedirectInteractBlockInternal$getMainHandStack(ItemStack instance) {
        return Paragon.inventoryManager.isDesynced() ? Paragon.inventoryManager.getServerStack().isEmpty() : instance.isEmpty();
    }

    @Override
    public void hookSendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator) {
        sendSequencedPacket(world, packetCreator);
    }
}
