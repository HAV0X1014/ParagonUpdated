package com.paragon.client.modules.movement

import com.paragon.backend.event.events.mc.TickEvent
import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.gui.screen.ingame.AnvilScreen
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.util.math.MathHelper
import org.lwjgl.glfw.GLFW

/**
 * @author aesthetical
 * @since 02/18/23
 */
object InventoryMove : Module("Inventory Move", "Lets you walk around in inventories", Category.MOVEMENT) {

    private val arrowRotate by bool("Arrow Rotate", true, "If to rotate in guis with the arrow keys")
    private val speed by float("Rotate Speed", 5.0f, 0.1f, 1.0f..20.0f, "The speed to rotate with the arrow keys")

    private var bindings: Array<KeyBinding>? = null

    override fun disable() {
        super.disable()
        bindings = null
    }

    @EventListener
    fun onTick(event: TickEvent?) {
        if (mc.currentScreen != null && mc.currentScreen !is ChatScreen && mc.currentScreen !is AnvilScreen && mc.currentScreen !is CreativeInventoryScreen) {
            if (bindings == null) {
                bindings = arrayOf(mc.options.forwardKey, mc.options.backKey, mc.options.rightKey, mc.options.leftKey)
                return
            }

            val handle = mc.window.handle

            for (binding in bindings!!) {
                binding.isPressed = InputUtil.isKeyPressed(handle, binding.defaultKey.code)
            }

            if (arrowRotate) {
                if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_UP)) {
                    mc.player!!.pitch = mc.player!!.pitch - speed
                } else if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_DOWN)) {
                    mc.player!!.pitch = mc.player!!.pitch + speed
                } else if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT)) {
                    mc.player!!.yaw = mc.player!!.yaw + speed
                } else if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT)) {
                    mc.player!!.yaw = mc.player!!.yaw - speed
                }

                mc.player!!.pitch = MathHelper.clamp(mc.player!!.pitch, -90.0f, 90.0f)
            }
        }
    }

}