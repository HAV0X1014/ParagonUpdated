package com.paragon.client.modules.visual

import com.paragon.backend.event.events.render.GameRenderEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.backend.setting.Colour
import com.paragon.util.calculations.MathsUtil
import com.paragon.util.getClientColour
import com.paragon.util.mc
import com.paragon.util.rendering.Renderer
import me.bush.eventbus.annotation.EventListener
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity

/**
 * @author surge, aesthetical
 * @since 12/02/2023
 */
object Tracers : Module("Tracers", "Draws lines to entities", Category.VISUAL) {

    private val passive by bool("Passive", true, "Draw lines to passive entities")
    private val hostile by bool("Hostile", true, "Draw lines to hostile entities")
    private val players by bool("Players", true, "Draw lines to players")
    private val lineWidth by float("Line Width", 1.5f, 0.1f, 0.1f..5.0f, "The width of the tracer line")

    private val stem by bool("Stem", false, "If to draw a stem on the entity")
    private val stemFade by bool("Stem Fade", true, "Fade the stem at the top") visibility { stem }

    private val alpha by int("Alpha", 50, 1, 0..255)

    @EventListener
    fun onGameRender(event: GameRenderEvent) {
        filtered().forEach { (entity: Entity, colour: Colour?) ->
            val entityPosition = MathsUtil.interpolate(entity, event.tickDelta)

            Renderer
                .submit(Renderer.line(event.matrices, Renderer.crosshair(), entityPosition, lineWidth, colour))
                .optional(stem, Renderer.line(event.matrices, entityPosition, entityPosition.add(0.0, entity.height.toDouble(), 0.0), lineWidth, colour, secondary = colour.integrateAlpha(if (stemFade) 0 else 255)))
        }
    }

    private fun filtered(): Map<Entity, Colour> {
        val entities: MutableMap<Entity, Colour> = HashMap()

        mc.world!!.entities.filter { it is PassiveEntity && passive || it is HostileEntity && hostile || it is PlayerEntity && players && it != mc.player }.forEach {
            entities[it] = when (it) {
                is PassiveEntity -> Colour(0, 255, 0, alpha)
                is HostileEntity -> Colour(255, 0, 0, alpha)
                is PlayerEntity -> getClientColour().integrateAlpha(alpha)
                else -> Colour(0, 0, 0, 0)
            }
        }

        return entities
    }

}