package com.paragon.mixin.duck;

/**
 * @author aesthetical
 * @since 02/17/23
 */
public interface IVec3d {

    void setX(double xIn);
    void setY(double yIn);
    void setZ(double zIn);

    void set(double xIn, double yIn, double zIn);
    void set(double xIn, double zIn);

}
