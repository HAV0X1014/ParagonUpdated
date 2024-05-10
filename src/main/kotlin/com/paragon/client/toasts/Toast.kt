package com.paragon.client.toasts

import com.paragon.backend.setting.Colour
import com.paragon.util.getClientColour
import com.paragon.util.rendering.NVGWrapper
import me.surge.animation.Animation
import me.surge.animation.Easing
import java.awt.Color

/**
 * @author aesthetical
 * @since 02/19/23
 */
class Toast(val toastType: ToastType, var deployer: String, var content: String, val lifespan: Long) {

    var endTime: Long = System.currentTimeMillis() + lifespan
    var isDead = false
        private set

    val animation = Animation(200f, true, Easing.CUBIC_IN_OUT)

    fun updateAndRender(nvg: NVGWrapper, posY: Float, screenWidth: Float) {
        if (System.currentTimeMillis() > endTime) {
            animation.state = false

            if (animation.animationFactor <= 0.0) {
                isDead = true
            }
        } else {
            animation.state = true
        }

        val width: Float = nvg.textWidth(content, size = 14.0f)

        val x: Float = screenWidth - 8.0f - (width * animation.animationFactor).toFloat()

        nvg.roundedQuad(x - 4.0f, posY, width + 8.0f, 50.0f, 4.5f, Colour(23, 23, 23, 185))

        val percent = (endTime - System.currentTimeMillis()).toDouble() / lifespan.toDouble()
        val barWidth = ((width + 8.0f) * percent).toFloat()

        val colour = when (toastType) {
            ToastType.ERROR -> Colour(245, 66, 66, 255)
            ToastType.SUCCESS -> Colour(66, 245, 117, 255)
            ToastType.WARNING -> Colour(245, 126, 66, 255)
            else -> getClientColour()
        }

        nvg.scissor(x - 4.0f, posY + 46f, barWidth, 4f) {
            nvg.roundedQuad(x - 4.0f, posY, barWidth.coerceAtLeast(9f), 50.0f, 4.5f, colour)
        }

        nvg.text(deployer, x, posY + 9.0F, Color.white, size = 16.0f, shadow = false)
        nvg.text(content, x, posY + 28.0f, Color.white, size = 14.0f, shadow = false)
    }

}