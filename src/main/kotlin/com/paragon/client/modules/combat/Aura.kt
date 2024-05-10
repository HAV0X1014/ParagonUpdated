package com.paragon.client.modules.combat

import com.paragon.Paragon
import com.paragon.backend.event.EventEra
import com.paragon.backend.event.events.move.MoveUpdateEvent
import com.paragon.backend.event.events.render.GameRenderEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.calculations.rotation.RotationUtil.calcToEntity
import com.paragon.util.calculations.rotation.Target
import com.paragon.util.getClientColour
import com.paragon.util.mc
import com.paragon.util.rendering.Renderer
import com.paragon.util.unattackables
import me.bush.eventbus.annotation.EventListener
import net.minecraft.entity.LivingEntity
import net.minecraft.registry.Registries
import net.minecraft.util.Hand
import kotlin.math.cos

/**
 * @author surge, aesthetical
 * @since 11/02/2023
 */
object Aura : Module("Aura", "Automatically attacks entities", Category.COMBAT) {

    private val filter = registry("Filter", Registries.ENTITY_TYPE, true, "Which entities to attack").also {
        unattackables.forEach { type ->
            it.setState(type, false)
        }
    }

    private val era by enum("Era", EventEra.PRE, "The time to attack at")
    private val mode by enum("Mode", Mode.SWITCH, "How to target entities")
    private val sort by enum("Sort", Sort.HEALTH, "How to sort targets")
    private val range by double("Range", 4.0, 0.1, 1.0..6.0, "How far to attack from")
    private val wallRange by double("Wall Range", 3.0, 0.1, 1.0..6.0, "How far to attack from through walls")
    private val rotate by bool("Rotate", true, "If to rotate towards the entity")
    private val target by enum("Target", Target.TORSO, "Where to rotate on the entity")
    private val render by enum("Render", DrawMode.BOTH, "Render a polygon around the target")
    private val sides by int("Sides", 8, 1, 3..120, "The amount of sides the polygon has") visibility { render != DrawMode.NONE }
    private val alpha by int("Alpha", 100, 1, 0..255, "The alpha of the fill") visibility { render == DrawMode.FILL || render == DrawMode.BOTH }

    private var current: LivingEntity? = null

    override val info = { mode.name + ", " + range }

    override fun disable() {
        current = null
    }

    @EventListener(recieveCancelled = true)
    fun onWalkingUpdate(event: MoveUpdateEvent) {
        if (!isValidTarget(current) || mode == Mode.SWITCH) {
            current = null

            val entities: MutableList<LivingEntity?> = ArrayList()

            for (entity in mc.world!!.entities) {
                if (entity is LivingEntity && isValidTarget(entity)) {
                    entities.add(entity)
                }
            }

            if (entities.isNotEmpty()) {
                current = entities.stream()
                    .min(sort.comparator)
                    .orElse(null)
            }
        }

        if (current != null) {
            if (rotate) {
                Paragon.rotationManager.submit(calcToEntity(current!!, target))
            }

            if (event.era == era && mc.player!!.getAttackCooldownProgress(1.0f) == 1.0f) {
                mc.interactionManager!!.attackEntity(mc.player, current)
                mc.player!!.swingHand(Hand.MAIN_HAND)
            }
        }
    }

    @EventListener(recieveCancelled = true)
    fun onGameRender(event: GameRenderEvent) {
        if (render != DrawMode.NONE && current != null) {
            if (render == DrawMode.FILL || render == DrawMode.BOTH) {
                Renderer.polygon(event.matrices, current!!.pos.add(0.0, current!!.height * (0.5 * (cos((mc.player!!.age * 4) * (Math.PI / 180)) + 1)), 0.0), current!!.width.toDouble(), getClientColour().integrateAlpha(alpha), Renderer.DrawMode.FILL, sides)
            }

            if (render == DrawMode.OUTLINE || render == DrawMode.BOTH) {
                Renderer.polygon(event.matrices, current!!.pos.add(0.0, current!!.height * (0.5 * (cos((mc.player!!.age * 4) * (Math.PI / 180)) + 1)), 0.0), current!!.width.toDouble(), getClientColour(), Renderer.DrawMode.LINES, sides)
            }
        }
    }

    private fun isValidTarget(entity: LivingEntity?): Boolean {
        if (entity == null || entity == mc.player || entity.isDead || entity.health <= 0.0f) {
            return false
        }

        if (entity.type !in filter.enabled()) {
            return false
        }

        val dist = if (mc.player!!.canSee(entity)) range else wallRange

        return mc.player!!.squaredDistanceTo(entity) <= dist * dist
    }

    enum class Mode {
        SINGLE,
        SWITCH
    }

    enum class Sort(val comparator: Comparator<LivingEntity?>) {
        HEALTH(Comparator.comparingDouble { obj: LivingEntity? -> obj!!.health.toDouble() }),
        DISTANCE(Comparator.comparingDouble { entity: LivingEntity? -> entity!!.distanceTo(mc.player).toDouble() })
    }

    enum class DrawMode {
        FILL,
        OUTLINE,
        BOTH,
        NONE
    }

}