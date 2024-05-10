package com.paragon.client.modules.player

import com.paragon.Paragon
import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.event.events.render.GameRenderEvent
import com.paragon.backend.managers.placement.PlacementData
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.box
import com.paragon.util.getClientColour
import com.paragon.util.getPlaceableSide
import com.paragon.util.mc
import com.paragon.util.rendering.Renderer
import me.bush.eventbus.annotation.EventListener
import net.minecraft.item.BlockItem
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Direction

/**
 * @author surge
 * @since 12/03/2023
 */
object AirPlace : Module("Air Place", "Allows you to place blocks wherever you are looking", Category.PLAYER) {

    private val pause by int("Pause", 5, 1, 0..20, "The delay (in ticks) between placing blocks")

    private val box by enum("Box", Box.BOTH, "How to draw the box")
    private val alpha by int("Alpha", 100, 1, 0..255, "The alpha of the fill") visibility { box == Box.FILL || box == Box.BOTH }

    private var tick = 0
    private var result: HitResult? = null

    @EventListener
    fun onTick(event: TickEvent) {
        result = mc.cameraEntity!!.raycast(mc.interactionManager!!.reachDistance.toDouble(), 0f, false)

        if (result !is BlockHitResult || mc.player!!.mainHandStack.item !is BlockItem) {
            return
        }

        if (mc.options.useKey.isPressed) {
            if (tick >= pause) {
                Paragon.placementManager.submit(
                    (result as BlockHitResult).blockPos,
                    PlacementData(
                        mc.player!!.inventory.selectedSlot,
                        (result as BlockHitResult).blockPos.getPlaceableSide() ?: Direction.UP
                    )
                )

                tick = 0
            } else {
                tick++
            }
        } else {
            tick = 0
        }
    }

    @EventListener
    fun onGameRender(event: GameRenderEvent) {
        if (box == Box.NONE) {
            return
        }

        result?.let {
            it as BlockHitResult

            if (box == Box.FILL || box == Box.BOTH) {
                Renderer.box(event.matrices, it.blockPos.box(), getClientColour().integrateAlpha(alpha), Renderer.DrawMode.FILL)
            }

            if (box == Box.OUTLINE || box == Box.BOTH) {
                Renderer.box(event.matrices, it.blockPos.box(), getClientColour(), Renderer.DrawMode.LINES)
            }
        }
    }

    private enum class Box {
        FILL,
        OUTLINE,
        BOTH,
        NONE
    }

}