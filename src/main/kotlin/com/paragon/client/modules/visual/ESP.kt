package com.paragon.client.modules.visual

import com.paragon.backend.event.events.render.GameRenderEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.backend.setting.Colour
import com.paragon.util.getClientColour
import com.paragon.util.mc
import com.paragon.util.rendering.Renderer
import me.bush.eventbus.annotation.EventListener
import net.minecraft.entity.Entity
import net.minecraft.entity.mob.HostileEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity

/**
 * @author surge
 * @since 22/02/2023
 */
object ESP : Module("ESP", "Highlights entities in the world", Category.VISUAL) {

    private val box by enum("Box", Box.BOTH, "How to draw the box")
    private val passive by bool("Passive", true, "Highlight passive entities")
    private val hostile by bool("Hostile", true, "Highlight hostile entities")
    private val players by bool("Players", true, "Highlight players")
    private val alpha by int("Alpha", 100, 1, 0..255, "The alpha of the fill") visibility { box == Box.FILL || box == Box.BOTH }

    @EventListener
    fun onGameRender(event: GameRenderEvent) {
        filtered().forEach { (entity, colour) ->
            if (box == Box.FILL || box == Box.BOTH) {
                Renderer.box(event.matrices, entity.boundingBox, colour.integrateAlpha(alpha), Renderer.DrawMode.FILL)
            }

            if (box == Box.OUTLINE || box == Box.BOTH) {
                Renderer.box(event.matrices, entity.boundingBox, colour, Renderer.DrawMode.LINES)
            }
        }
    }

    private fun filtered(): Map<Entity, Colour> {
        val entities: MutableMap<Entity, Colour> = HashMap()

        mc.world!!.entities.filter { it is PassiveEntity && passive || it is HostileEntity && hostile || it is PlayerEntity && players && it != mc.player }.forEach {
            entities[it] = when (it) {
                is PassiveEntity -> Colour(0, 255, 0, 255)
                is HostileEntity -> Colour(255, 0, 0, 255)
                is PlayerEntity -> getClientColour().integrateAlpha(255)
                else -> Colour(0, 0, 0, 0)
            }
        }

        return entities
    }

    private enum class Box {
        FILL,
        OUTLINE,
        BOTH
    }

}