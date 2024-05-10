package com.paragon.mixin.mixins.net;

import com.paragon.Paragon;
import com.paragon.backend.event.events.net.PacketEvent;
import com.paragon.backend.event.events.net.PacketEvent.Inbound;
import com.paragon.backend.event.events.net.PacketEvent.Outbound;
import com.paragon.mixin.duck.IClientConnection;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author aesthetical
 * @since 02/17/23
 */
@Mixin(ClientConnection.class)
public abstract class MixinClientConnection implements IClientConnection {

    @Shadow
    protected abstract void sendImmediately(Packet<?> packet, @Nullable PacketCallbacks callbacks);

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    public void hookSend(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo info) {
        PacketEvent.Outbound outbound = new Outbound(packet);
        if (Paragon.bus.post(outbound)) {
            info.cancel();
        }
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void hookChannelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo info) {
        PacketEvent.Inbound inbound = new Inbound(packet);
        if (Paragon.bus.post(inbound)) {
            info.cancel();
        }
    }

    @Override
    public void sendPacketNoEvent(Packet<?> packet) {
        sendImmediately(packet, null);
    }

}
