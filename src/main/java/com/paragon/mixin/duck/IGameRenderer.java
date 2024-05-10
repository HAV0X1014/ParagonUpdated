package com.paragon.mixin.duck;

import net.minecraft.client.util.math.MatrixStack;

/**
 * @author aesthetical
 * @since 02/19/23
 */
public interface IGameRenderer {

    void hookBobView(MatrixStack matrices, float tickDelta);

}
