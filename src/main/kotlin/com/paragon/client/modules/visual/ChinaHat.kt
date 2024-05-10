package com.paragon.client.modules.visual

import com.paragon.backend.event.events.render.GameRenderEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.calculations.MathsUtil
import com.paragon.util.getClientColour
import com.paragon.util.mc
import com.paragon.util.rendering.Renderer
import me.bush.eventbus.annotation.EventListener
import net.minecraft.client.option.Perspective
import net.minecraft.util.math.Vec3d

/**
 * @author surge
 * @since 06/03/2023
 */
object ChinaHat : Module("China Hat", "-1000 social credit", Category.VISUAL) {

    private val offset by double("Offset", -0.25, 0.01, -2.0..0.0, "The Y offset of the outside edge")
    private val alpha by int("Alpha", 150, 1, 0..255, "The alpha of the fill")

    @EventListener
    fun onGameRender(event: GameRenderEvent) {
        mc.world!!.players.forEach {
            if (it != mc.player || mc.options.perspective != Perspective.FIRST_PERSON) {
                val pos = MathsUtil.interpolate(it, event.tickDelta)

                Renderer.polygon(event.matrices, pos.add(0.0, it.height.toDouble() + 0.25,  0.0), 0.65, getClientColour().integrateAlpha(alpha), Renderer.DrawMode.FILL, 120, offset)
                Renderer.polygon(event.matrices, pos.add(0.0, it.height.toDouble() + 0.25,  0.0), 0.65, getClientColour(), Renderer.DrawMode.LINES, 120, offset)
            }
        }
    }

}