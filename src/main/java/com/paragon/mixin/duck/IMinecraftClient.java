package com.paragon.mixin.duck;

import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Session;

/**
 * @author aesthetical
 * @since 02/17/23
 */
public interface IMinecraftClient {
    RenderTickCounter getRenderTickCounter();

    void setItemUseCooldown(int itemUseCooldown);

    void setSession(Session session);
}
