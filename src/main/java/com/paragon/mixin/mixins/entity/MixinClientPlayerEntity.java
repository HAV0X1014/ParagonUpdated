package com.paragon.mixin.mixins.entity;

import com.mojang.authlib.GameProfile;
import com.paragon.Paragon;
import com.paragon.backend.event.EventEra;
import com.paragon.backend.event.events.input.control.ItemSlowdownEvent;
import com.paragon.backend.event.events.input.control.SneakSlowdownEvent;
import com.paragon.backend.event.events.move.MoveEvent;
import com.paragon.backend.event.events.move.MoveUpdateEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author aesthetical
 * @since 02/17/23
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

    @Shadow private double lastX;
    @Shadow private double lastBaseY;
    @Shadow private double lastZ;
    @Shadow private float lastYaw;
    @Shadow private float lastPitch;

    @Shadow private boolean lastSneaking;
    @Shadow private boolean lastOnGround;
    @Shadow private boolean autoJumpEnabled;

    @Shadow @Final protected MinecraftClient client;
    @Shadow @Final public ClientPlayNetworkHandler networkHandler;
    @Shadow public Input input;

    @Shadow private int ticksSinceLastPositionPacketSent;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow protected abstract boolean isCamera();
    @Shadow protected abstract void sendSprintingPacket();

    @Shadow public float renderPitch;

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    public void hookSendMovementPacketsPre(CallbackInfo info) {
        MoveUpdateEvent event = new MoveUpdateEvent(EventEra.PRE,
                getX(), getY(), getZ(), getYaw(), getPitch(), isOnGround());
        if (Paragon.bus.post(event)) {
            info.cancel();
            handleMovementPackets(event);
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    public void hookSendMovementPacketsPost(CallbackInfo info) {
        Paragon.bus.post(new MoveUpdateEvent(EventEra.POST,
                getX(), getY(), getZ(), getYaw(), getPitch(), isOnGround()));
    }

    @Redirect(
            method = "tickMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
                    ordinal = 0))
    public boolean hookTickMovement(ClientPlayerEntity instance) {
        if (Paragon.bus.post(new ItemSlowdownEvent((ClientPlayerEntity) (Object) this))) {
            return false;
        }

        return instance.isUsingItem();
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    public void hookShouldSlowDown(CallbackInfoReturnable<Boolean> info) {
        if (Paragon.bus.post(new SneakSlowdownEvent((ClientPlayerEntity) (Object) this))) {
            info.setReturnValue(false);
        }
    }

    @Redirect(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    public void redirectMove$Move(AbstractClientPlayerEntity instance, MovementType movementType, Vec3d vec3d) {
        MoveEvent event = new MoveEvent(vec3d);
        Paragon.bus.post(event);
        super.move(movementType, event.getMotionVec());
    }

    private void handleMovementPackets(MoveUpdateEvent event) {

        sendSprintingPacket();

        if (isSneaking() != lastSneaking) {
            networkHandler.sendPacket(new ClientCommandC2SPacket(this, isSneaking()
                    ? ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY : ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            lastSneaking = isSneaking();
        }

        if (isCamera()) {
            double d = event.x - lastX;
            double e = event.y - lastBaseY;
            double f = event.z - lastZ;
            double g = event.yaw - lastYaw;
            double h = event.pitch - lastPitch;

            ++ticksSinceLastPositionPacketSent;

            boolean moved = MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(2.0E-4) || ticksSinceLastPositionPacketSent >= 20;
            boolean rotated = g != 0.0 || h != 0.0;

            if (hasVehicle()) {
                Vec3d vec3d = getVelocity();
                networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0, vec3d.z, event.yaw, event.pitch, event.onGround));
                moved = false;
            } else if (moved && rotated) {
                networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(event.x, event.y, event.z, event.yaw, event.pitch, event.onGround));
            } else if (moved) {
                networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(event.x, event.y, event.z, event.onGround));
            } else if (rotated) {
                networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(event.yaw, event.pitch, event.onGround));
            } else if (lastOnGround != event.onGround) {
                networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(event.onGround));
            }

            if (moved) {
                lastX = event.x;
                lastBaseY = event.y;
                lastZ = event.z;
                ticksSinceLastPositionPacketSent = 0;
            }

            if (rotated) {
                lastYaw = event.yaw;
                lastPitch = event.pitch;
            }

            lastOnGround = onGround;
            autoJumpEnabled = client.options.getAutoJump().getValue();
        }

        Paragon.bus.post(new MoveUpdateEvent(EventEra.POST,
                event.x, event.y, event.z, event.yaw, event.pitch, event.onGround));
    }
}
