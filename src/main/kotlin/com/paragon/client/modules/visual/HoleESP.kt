package com.paragon.client.modules.visual

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.event.events.render.GameRenderEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.backend.setting.Colour
import com.paragon.util.backgroundThread
import com.paragon.util.box
import com.paragon.util.mc
import com.paragon.util.rendering.Renderer
import me.bush.eventbus.annotation.EventListener
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.sqrt

/**
 * @author surge
 * @since 22/02/2023
 */
object HoleESP : Module("Hole ESP", "Highlights safe holes", Category.VISUAL) {

    private val range by int("Range", 5, 1, 1..10, "The range to search for holes in")
    private val box by enum("Box", Box.BOTH, "How to draw the box")
    private val obsidian by bool("Passive", true, "Highlight holes surrounded by obsidian")
    private val mixed by bool("Hostile", true, "Highlight holes surrounded by obsidian or bedrock")
    private val bedrock by bool("Players", true, "Highlight holes surrounded by bedrock")
    private val alpha by int("Alpha", 100, 1, 0..255, "The alpha of the fill") visibility { box == Box.FILL || box == Box.BOTH }
    private val height by double("Height", 0.2, 0.01, 0.0..1.0, "The height of the box")
    private val dynamicHeight by bool("DynamicHeight", false, "Shrinks the boxes the closer they are to you")
    private val reverseDynamic by bool("ReverseDynamic", false, "Expands the boxes the closer they are to you") visibility { dynamicHeight }

    private val holes = ConcurrentHashMap<BlockPos, Type>()

    @EventListener
    fun onTick(event: TickEvent) {
        backgroundThread {
            for (x in -range..range) {
                for (y in -range..range) {
                    for (z in -range..range) {
                        val pos = BlockPos(mc.player!!.blockPos.x + x, mc.player!!.blockPos.y + y,mc.player!!.blockPos.z + z)

                        val type = getType(pos)

                        if (type != null) {
                            holes[pos] = type
                        }
                    }
                }
            }

            // hopefully prevent the flickering thing??

            val removeBuffer = mutableListOf<BlockPos>()

            holes.forEach { (pos, type) ->
                if (sqrt(pos.getSquaredDistance(mc.player!!.x, mc.player!!.y, mc.player!!.z)) > range || getType(pos) != type) {
                    removeBuffer.add(pos)
                }
            }

            removeBuffer.forEach {
                holes.remove(it)
            }
        }
    }

    @EventListener
    fun onGameRender(event: GameRenderEvent) {
        holes.forEach { (position, type) ->
            var bb = position.box()
            bb = bb.withMaxY(bb.minY + height)

            if (dynamicHeight) {
                bb = if (reverseDynamic) {
                    bb.withMaxY(bb.minY + (height * (1 - (sqrt(position.getSquaredDistance(mc.player!!.x, mc.player!!.y, mc.player!!.z)) / range))))
                } else {
                    bb.withMaxY(bb.minY + (height * (sqrt(position.getSquaredDistance(mc.player!!.x, mc.player!!.y, mc.player!!.z)) / range)))
                }
            }

            if (box == Box.FILL || box == Box.BOTH) {
                Renderer.box(event.matrices, bb, type.colour.integrateAlpha(alpha), Renderer.DrawMode.FILL)
            }

            if (box == Box.OUTLINE || box == Box.BOTH) {
                Renderer.box(event.matrices, bb, type.colour, Renderer.DrawMode.LINES)
            }
        }
    }

    fun getType(position: BlockPos): Type? {
        if (!mc.world!!.getBlockState(position).isReplaceable || mc.world!!.getBlockState(position.down()).isReplaceable) {
            return null
        }

        val surrounding = arrayListOf(
            mc.world!!.getBlockState(position.north()),
            mc.world!!.getBlockState(position.east()),
            mc.world!!.getBlockState(position.south()),
            mc.world!!.getBlockState(position.west())
        )

        if (surrounding.all { it.block == Blocks.OBSIDIAN } && obsidian) {
            return Type.OBSIDIAN
        } else if (surrounding.all { it.block == Blocks.BEDROCK } && bedrock) {
            return Type.BEDROCK
        } else if (surrounding.all { it.block == Blocks.OBSIDIAN || it.block == Blocks.BEDROCK } && mixed) {
            return Type.MIXED
        }

        return null
    }

    enum class Type(val colour: Colour) {
        OBSIDIAN(Colour(255, 0, 0, 255)),
        MIXED(Colour(255, 140, 0, 255)),
        BEDROCK(Colour(0, 255, 0, 255))
    }

    private enum class Box {
        FILL,
        OUTLINE,
        BOTH
    }

}