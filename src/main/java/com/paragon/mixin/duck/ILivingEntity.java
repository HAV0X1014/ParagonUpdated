package com.paragon.mixin.duck;

/**
 * @author aesthetical
 * @since 02/19/23
 */
public interface ILivingEntity {
    int getLastAttackedTicks();

    float[] getRenderRotations();
    float[] getPreviousRenderRotations();
}
