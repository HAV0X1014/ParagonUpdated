package com.paragon.client.modules.player

import com.mojang.authlib.GameProfile
import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import com.paragon.util.nullCheck
import me.bush.eventbus.annotation.EventListener
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.Entity.RemovalReason
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
import net.minecraft.util.Hand
import java.util.*

/**
 * @author aesthetical
 * @since 02/19/23
 */
object FakePlayer : Module("Fake Player", "Spawns a fake player", Category.PLAYER) {

    private var fake: EntityFakePlayer? = null

    override fun enable() {
        if (nullCheck()) {
            toggle()
            return
        }

        fake = EntityFakePlayer(mc.world!!, GameProfile(UUID.randomUUID(), "Fake"))

        fake!!.copyPositionAndRotation(mc.player)
        fake!!.inventory.clone(mc.player!!.inventory)
        fake!!.id = -137769420

        mc.world!!.spawnEntity(fake)
        mc.world!!.addEntity(fake!!.id, fake)
    }

    override fun disable() {
        if (!nullCheck()) {
            mc.world!!.removeEntity(fake!!.id, RemovalReason.KILLED)
        }

        fake = null
    }

    @EventListener
    fun onTick(event: TickEvent?) {
        if (mc.player!!.age < 5) {
            toggle()
            return
        }
    }

    private class EntityFakePlayer(clientWorld: ClientWorld, gameProfile: GameProfile) : OtherClientPlayerEntity(clientWorld, gameProfile) {

        override fun damage(source: DamageSource, amount: Float): Boolean {
            health -= amount
            return true
        }

        override fun tick() {
            super.tick()

            if (getStackInHand(Hand.OFF_HAND).item != Items.TOTEM_OF_UNDYING) {
                setStackInHand(Hand.OFF_HAND, ItemStack(Items.TOTEM_OF_UNDYING))
            }

            if (health <= 0.0f) {
                mc.networkHandler!!.onEntityStatus(EntityStatusS2CPacket(this, 35.toByte()))

                health = 1.0f
                absorptionAmount = 8.0f

                clearActiveItem()
                addStatusEffect(StatusEffectInstance(StatusEffects.REGENERATION, 900, 1))
                addStatusEffect(StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1))
            }
        }

        override fun isAttackable(): Boolean {
            return true
        }

        override fun handleAttack(attacker: Entity): Boolean {
            return false
        }

        override fun canTakeDamage(): Boolean {
            return true
        }
    }

}