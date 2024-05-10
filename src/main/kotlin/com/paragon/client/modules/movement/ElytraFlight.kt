package com.paragon.client.modules.movement

import com.paragon.Paragon
import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.event.events.move.MoveEvent
import com.paragon.backend.event.events.paragon.SettingUpdateEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.backend.setting.Setting
import com.paragon.backend.setting.SettingContainer
import com.paragon.mixin.duck.IVec3d
import com.paragon.util.calculations.Timer
import com.paragon.util.inventory.InventoryUtil
import com.paragon.util.mc
import com.paragon.util.nullCheck
import com.paragon.util.truncate
import me.bush.eventbus.annotation.EventListener
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.FireworkRocketItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.TridentItem
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author surge
 * @since 13/03/2023
 */
object ElytraFlight : Module("Elytra Flight", "Allows you to fly easier with elytra", Category.MOVEMENT) {

    private var modeInstance: Setting<Mode>

    private val mode by enum("Mode", Mode.VANILLA, "How to apply easier flight").also {
        modeInstance = it
    }

    private val vanillaSettings = object : SettingContainer() {

        val horizontalSpeed by double("Horizontal", 1.0, 0.1, 0.1..5.0, "How fast you move horizontally")
        val verticalSpeed by double("Vertical", 1.0, 0.1, 0.1..5.0, "How fast you move vertically")

    }.also {
        it.visibleWhen { mode == Mode.VANILLA }
        register(it)
    }

    private val fireworkSettings = object : SettingContainer() {

        val delay by double("Delay", 10.0, 0.1, 1.0..20.0, "The delay (in seconds) between using firework rockets")
        val resetIdle by bool("Reset Idle", false, "Reset the timer when not flying")
        val autoSwap by enum("Auto Swap", AutoSwap.CLIENT, "How to swap to rockets")
        val swapBack by bool("Swap Back", true, "Automatically swap back to your original slot") visibility { autoSwap != AutoSwap.NONE }
        val substituteTrident by bool("Substitute Trident", true, "Use a trident in place of fireworks if the conditions are met")

    }.also {
        it.visibleWhen { mode == Mode.FIREWORK }
        register(it)
    }

    private val autoTakeoff by bool("Auto Takeoff", true, "Automatically starts flying when you press the jump button")

    private val takeoffSettings = object : SettingContainer() {

        val delay by int("Takeoff Delay", 8, 1, 0..20, "How many ticks to wait before flying")

    }.also {
        it.visibleWhen { autoTakeoff }
        register(it)
    }

    private var jumpDelay = 0

    override val info = {
        when (mode) {
            Mode.VANILLA -> ""
            else -> (fireworkSettings.delay - (FireworkProcessor.timer.timeMs() / 1000f)).truncate(1)
        }
    }

    override fun postLoad() {
        if (this.isEnabled) {
            this.mode.processor.enable()
        }
    }

    override fun enable() {
        jumpDelay = 0
        this.mode.processor.enable()
    }

    override fun disable() {
        if (!nullCheck()) {
            Paragon.inventoryManager.sync()
        }

        this.mode.processor.disable()
    }

    @EventListener
    fun onSettingUpdate(event: SettingUpdateEvent) {
        if (event.setting == modeInstance) {
            this.mode.processor.disable()
        }
    }

    @EventListener
    fun onTick(event: TickEvent) {
        if (autoTakeoff && mc.options.jumpKey.isPressed && !mc.player!!.isFallFlying && mc.player!!.armorItems.any { it.item == Items.ELYTRA && it.damage != it.maxDamage }) {
            if (jumpDelay < takeoffSettings.delay) {
                jumpDelay++
            } else {
                jumpDelay = 0

                if (mc.player!!.isOnGround) {
                    mc.player!!.setJumping(false)
                    mc.player!!.jump()
                }

                mc.networkHandler!!.sendPacket(ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
                mc.player!!.startFallFlying()

                Paragon.toastManager.info(this.name, "Deploying Elytra!", 2000L)
            }
        } else {
            jumpDelay = 0
        }

        mode.processor.onTick()
    }

    private abstract class ModeProcessor {

        fun enable() {
            Paragon.bus.subscribe(this)
        }

        fun disable() {
            Paragon.bus.unsubscribe(this)
        }

        abstract fun onTick()

    }

    private object VanillaProcessor : ModeProcessor() {

        override fun onTick() {}

        @EventListener
        fun onMove(event: MoveEvent) {
            if (mc.player!!.isFallFlying) {
                var forward = if (mc.options.forwardKey.isPressed) 1f else if (mc.options.backKey.isPressed) -1f else 0f
                var strafe = if (mc.options.leftKey.isPressed) 1f else if (mc.options.rightKey.isPressed) -1f else 0f
                val vertical = if (mc.options.jumpKey.isPressed) 1f else if (mc.options.sneakKey.isPressed) -1f else 0f

                var playerYaw = mc.player!!.yaw

                if (forward != 0f) {
                    if (strafe >= 1) {
                        playerYaw += (if (forward > 0) -45 else 45).toFloat()
                        strafe = 0f
                    } else if (strafe <= -1) {
                        playerYaw += (if (forward > 0) 45 else -45).toFloat()
                        strafe = 0f
                    }

                    forward = if (forward > 0) {
                        1f
                    } else {
                        -1f
                    }
                }

                val sin = sin(Math.toRadians((playerYaw + 90).toDouble()))
                val cos = cos(Math.toRadians((playerYaw + 90).toDouble()))

                val velocity = Vec3d(forward.toDouble() * vanillaSettings.horizontalSpeed * cos + strafe.toDouble() * vanillaSettings.horizontalSpeed * sin, vertical * vanillaSettings.verticalSpeed, forward.toDouble() * vanillaSettings.horizontalSpeed * sin - strafe.toDouble() * vanillaSettings.horizontalSpeed * cos)

                event.setX(velocity.x)
                event.setY(velocity.y)
                event.setZ(velocity.z)
            }
        }

    }

    private object FireworkProcessor : ModeProcessor() {

        val timer = Timer()

        override fun onTick() {
            if (mc.player!!.isFallFlying && !mc.isPaused) {
                if (timer.elapsed(fireworkSettings.delay, Timer.Format.SECONDS)) {
                    var hand = Hand.MAIN_HAND

                    val original = mc.player!!.inventory.selectedSlot

                    val tridentFound = InventoryUtil.find(InventoryUtil.HOTBAR) {
                        it.item is TridentItem && EnchantmentHelper.getRiptide(it) > 0
                    }

                    val shouldTrident = fireworkSettings.substituteTrident && tridentFound > -1 && mc.player!!.isTouchingWaterOrRain

                    if (InventoryUtil.hasStackIn(Hand.OFF_HAND, FireworkRocketItem::class.java)) {
                        hand = Hand.OFF_HAND
                    } else {
                        when (fireworkSettings.autoSwap) {
                            AutoSwap.NONE -> {
                                if (mc.player!!.mainHandStack.item !is FireworkRocketItem && shouldTrident && mc.player!!.mainHandStack.item !is TridentItem) {
                                    return
                                }
                            }

                            AutoSwap.CLIENT, AutoSwap.SERVER -> {
                                val slot = if (shouldTrident) tridentFound else nextSlot

                                if (slot == -1) {
                                    return
                                }

                                if (fireworkSettings.autoSwap == AutoSwap.CLIENT) {
                                    mc.player!!.inventory.selectedSlot = slot
                                } else {
                                    Paragon.inventoryManager.swap(slot)
                                }
                            }
                        }
                    }

                    if (shouldTrident) {
                        mc.player!!.mainHandStack.onStoppedUsing(mc.world!!, mc.player!!, 0)
                    } else {
                        val result = mc.interactionManager!!.interactItem(mc.player, hand)

                        if (result.shouldSwingHand()) {
                            mc.player!!.swingHand(hand)
                        }
                    }

                    if (fireworkSettings.swapBack && mc.player!!.inventory.selectedSlot != original && hand == Hand.MAIN_HAND) {
                        if (fireworkSettings.autoSwap == AutoSwap.CLIENT || fireworkSettings.autoSwap == AutoSwap.SERVER) {
                            if (fireworkSettings.autoSwap == AutoSwap.CLIENT) {
                                mc.player!!.inventory.selectedSlot = original
                            } else {
                                Paragon.inventoryManager.swap(original)
                            }
                        }
                    }

                    timer.reset()
                }
            } else if (fireworkSettings.resetIdle) {
                timer.reset()
            }
        }

        private val nextSlot: Int
            get() {
                var slot: Int = mc.player!!.inventory.selectedSlot
                var count = 0

                if (mc.player!!.inventory.getStack(slot).item is FireworkRocketItem) {
                    count = mc.player!!.inventory.getStack(slot).count
                } else {
                    slot = -1
                }

                for (i in 0..8) {
                    val stack: ItemStack = mc.player!!.inventory.getStack(i)

                    if (stack.item is FireworkRocketItem && stack.count > count) {
                        slot = i
                        count = stack.count
                    }
                }

                return slot
            }

    }

    private enum class Mode(val processor: ModeProcessor) {
        VANILLA(VanillaProcessor),
        FIREWORK(FireworkProcessor)
    }

    private enum class AutoSwap {
        NONE,
        CLIENT,
        SERVER
    }

}