package com.paragon.client.modules.visual

import com.paragon.Paragon
import com.paragon.backend.event.events.render.RenderHUDEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.BuildConfig.BuildConfig
import com.paragon.util.formatCapitalised
import com.paragon.util.getClientColour
import com.paragon.util.mc
import com.paragon.util.rendering.ColorUtil
import com.paragon.util.rendering.NVGWrapper
import me.bush.eventbus.annotation.EventListener
import me.surge.animation.BoundedAnimation
import me.surge.animation.Easing
import net.minecraft.client.gui.screen.ChatScreen
import java.awt.Color

/**
 * @author aesthetical
 * @since 02/17/23
 */
object HUD : Module("HUD", "Renders an overlay", Category.VISUAL) {

    private val yAnimation = BoundedAnimation(0.0f, 30.0f, 200.0f, false, Easing.CUBIC_IN_OUT)

    init {
        // auto-enable
        if (!isEnabled) {
            toggle()
        }

        visible = false
    }

    @EventListener
    fun onRenderHUD(event: RenderHUDEvent) {
        NVGWrapper.scope { r: NVGWrapper ->
            r.text("P", 4.0f, 4.0f, getClientColour(), size = 17f, shadow = true)
            r.text(
                "aragon " + BuildConfig.BRANCH + "/" + BuildConfig.HASH,
                4.0f + r.textWidth("P"),
                4.0f,
                Color.white,
                size = 17f,
                shadow = true
            )

            val enabled: List<Module> = Paragon.moduleManager.modules
                .filter { module: Module -> module.isActive() && module.visible }
                .sortedBy {
                    r.textWidth(it.name + if (it.info() == null) "" else " " + it.info()!!.replace("_", " "))
                }
                .reversed()

            if (enabled.isNotEmpty()) {
                var y = 4.0f
                if (mc.player!!.activeStatusEffects.isNotEmpty()) {
                    y += 52.0f
                }

                for (i in enabled.indices) {
                    val module = enabled[i]
                    val c = module.name
                    var textWidth = r.textWidth(c, size = 17.0f)

                    if (module.info() != null) {
                        textWidth += r.textWidth(" " + module.info()!!.replace("_", " ").formatCapitalised(), size = 17.0f)
                    }

                    module.animation.maximum = textWidth

                    val x = (event.width - textWidth * module.animation.animationFactor - 8.0f).toFloat()
                    val rectWidth = textWidth + 8.0f
                    val color = ColorUtil.gradientRainbow(getClientColour(), 0.72f, i * 200 + 150)

                    r.quad(x - 4.0f, y, rectWidth, (22.0f * module.animation.animationFactor).toFloat(), Color(0, 0, 0, 145))
                    r.quad(x + rectWidth - 6.0f, y, 2.0f, 22.0f, color)
                    r.text(c, x, y + 3, color, size = 17f, shadow = true)

                    if (module.info() != null) {
                        r.text(
                            module.info()!!.replace("_", " ").formatCapitalised(),
                            x + 2.0f + r.textWidth(module.name + " ", size = 17.0f),
                            y + 3,
                            Color.gray,
                            size = 17f,
                            shadow = true
                        )
                    }

                    y += (22.0f * module.animation.animationFactor).toFloat()
                }
            }

            yAnimation.state = mc.currentScreen is ChatScreen
            var y = event.height - 20.0f - (yAnimation.animationFactor.toFloat() * yAnimation.maximum)

            r.text("XYZ: ", 1.5f, y, Color.GRAY, shadow = true)
            r.text(String.format("%.1f", mc.player!!.x)
                    + " " + String.format("%.1f", mc.player!!.y) + " "
                    + String.format("%.1f", mc.player!!.z),
                    2.0f + r.textWidth("XYZ: "), y, Color.WHITE, shadow = true)

        }
    }

}