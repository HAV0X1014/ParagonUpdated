package com.paragon.mixin.mixins.math;

import com.paragon.mixin.duck.IVec3d;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author aesthetical
 * @since 02/17/23
 */
@Mixin(Vec3d.class)
public class MixinVec3d implements IVec3d {

    @Shadow @Final @Mutable
    public double x;

    @Shadow @Final @Mutable
    public double y;

    @Shadow @Final @Mutable
    public double z;

    @Override
    public void setX(double xIn) {
        x = xIn;
    }

    @Override
    public void setY(double yIn) {
        y = yIn;
    }

    @Override
    public void setZ(double zIn) {
        z = zIn;
    }

    @Override
    public void set(double xIn, double yIn, double zIn) {
        x = xIn;
        y = yIn;
        z = zIn;
    }

    @Override
    public void set(double xIn, double zIn) {
        x = xIn;
        z = zIn;
    }
}
