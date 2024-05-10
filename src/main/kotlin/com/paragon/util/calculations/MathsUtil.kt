package com.paragon.util.calculations

import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author surge
 * @since 11/02/2023
 */
object MathsUtil {

    @JvmStatic
    fun roundDouble(value: Double, scale: Int): Double {
        return BigDecimal(value).setScale(scale, RoundingMode.HALF_DOWN).toDouble()
    }

    fun interpolate(entity: Entity, partialTicks: Float): Vec3d {
        return Vec3d(entity.lastRenderX, entity.lastRenderY, entity.lastRenderZ).add(Vec3d((entity.x - entity.lastRenderX) * partialTicks, (entity.y - entity.lastRenderY) * partialTicks, (entity.z - entity.lastRenderZ) * partialTicks))
    }

}