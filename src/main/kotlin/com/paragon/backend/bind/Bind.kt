package com.paragon.backend.bind

import com.paragon.backend.ToggleFeature
import org.lwjgl.glfw.GLFW
import java.util.*

/**
 * @author surge, aesthetical
 * @since 11/02/2023
 */
class Bind(val feature: ToggleFeature, var type: DeviceType = DeviceType.KEYBOARD, var code: Int = GLFW.GLFW_KEY_UNKNOWN) {

    private var inhibitor: BindInhibitor? = null

    var state = false
        set(state) {
            field = state

            if (inhibitor != null) {
                inhibitor!!.act(feature, this)
            }
        }

    var isPersistent = true

    fun setInhibitor(inhibitor: BindInhibitor) {
        this.inhibitor = inhibitor
    }

    override fun toString(): String {
        return when (type) {
            DeviceType.MOUSE -> getMouseName(code)
            DeviceType.KEYBOARD -> getKeyName(code)
            else -> "?"
        }
    }

    companion object {
        /**
         * Completely and utterly YOINKED from meteor
         */
        fun getKeyName(key: Int): String {
            return when (key) {
                GLFW.GLFW_KEY_UNKNOWN -> {
                    "None"
                }

                GLFW.GLFW_KEY_ESCAPE -> {
                    "Esc"
                }

                GLFW.GLFW_KEY_GRAVE_ACCENT -> {
                    "Grave Accent"
                }

                GLFW.GLFW_KEY_WORLD_1 -> {
                    "World 1"
                }

                GLFW.GLFW_KEY_WORLD_2 -> {
                    "World 2"
                }

                GLFW.GLFW_KEY_PRINT_SCREEN -> {
                    "Print Screen"
                }

                GLFW.GLFW_KEY_PAUSE -> {
                    "Pause"
                }

                GLFW.GLFW_KEY_INSERT -> {
                    "Insert"
                }

                GLFW.GLFW_KEY_DELETE -> {
                    "Delete"
                }

                GLFW.GLFW_KEY_HOME -> {
                    "Home"
                }

                GLFW.GLFW_KEY_PAGE_UP -> {
                    "Page Up"
                }

                GLFW.GLFW_KEY_PAGE_DOWN -> {
                    "Page Down"
                }

                GLFW.GLFW_KEY_END -> {
                    "End"
                }

                GLFW.GLFW_KEY_TAB -> {
                    "Tab"
                }

                GLFW.GLFW_KEY_LEFT_CONTROL -> {
                    "Left Control"
                }

                GLFW.GLFW_KEY_RIGHT_CONTROL -> {
                    "Right Control"
                }

                GLFW.GLFW_KEY_LEFT_ALT -> {
                    "Left Alt"
                }

                GLFW.GLFW_KEY_RIGHT_ALT -> {
                    "Right Alt"
                }

                GLFW.GLFW_KEY_LEFT_SHIFT -> {
                    "Left Shift"
                }

                GLFW.GLFW_KEY_RIGHT_SHIFT -> {
                    "Right Shift"
                }

                GLFW.GLFW_KEY_UP -> {
                    "Arrow Up"
                }

                GLFW.GLFW_KEY_DOWN -> {
                    "Arrow Down"
                }

                GLFW.GLFW_KEY_LEFT -> {
                    "Arrow Left"
                }

                GLFW.GLFW_KEY_RIGHT -> {
                    "Arrow Right"
                }

                GLFW.GLFW_KEY_APOSTROPHE -> {
                    "Apostrophe"
                }

                GLFW.GLFW_KEY_BACKSPACE -> {
                    "Backspace"
                }

                GLFW.GLFW_KEY_CAPS_LOCK -> {
                    "Caps Lock"
                }

                GLFW.GLFW_KEY_MENU -> {
                    "Menu"
                }

                GLFW.GLFW_KEY_LEFT_SUPER -> {
                    "Left Super"
                }

                GLFW.GLFW_KEY_RIGHT_SUPER -> {
                    "Right Super"
                }

                GLFW.GLFW_KEY_ENTER -> {
                    "Enter"
                }

                GLFW.GLFW_KEY_KP_ENTER -> {
                    "Numpad Enter"
                }

                GLFW.GLFW_KEY_NUM_LOCK -> {
                    "Num Lock"
                }

                GLFW.GLFW_KEY_SPACE -> {
                    "Space"
                }

                GLFW.GLFW_KEY_F1 -> {
                    "F1"
                }

                GLFW.GLFW_KEY_F2 -> {
                    "F2"
                }

                GLFW.GLFW_KEY_F3 -> {
                    "F3"
                }

                GLFW.GLFW_KEY_F4 -> {
                    "F4"
                }

                GLFW.GLFW_KEY_F5 -> {
                    "F5"
                }

                GLFW.GLFW_KEY_F6 -> {
                    "F6"
                }

                GLFW.GLFW_KEY_F7 -> {
                    "F7"
                }

                GLFW.GLFW_KEY_F8 -> {
                    "F8"
                }

                GLFW.GLFW_KEY_F9 -> {
                    "F9"
                }

                GLFW.GLFW_KEY_F10 -> {
                    "F10"
                }

                GLFW.GLFW_KEY_F11 -> {
                    "F11"
                }

                GLFW.GLFW_KEY_F12 -> {
                    "F12"
                }

                GLFW.GLFW_KEY_F13 -> {
                    "F13"
                }

                GLFW.GLFW_KEY_F14 -> {
                    "F14"
                }

                GLFW.GLFW_KEY_F15 -> {
                    "F15"
                }

                GLFW.GLFW_KEY_F16 -> {
                    "F16"
                }

                GLFW.GLFW_KEY_F17 -> {
                    "F17"
                }

                GLFW.GLFW_KEY_F18 -> {
                    "F18"
                }

                GLFW.GLFW_KEY_F19 -> {
                    "F19"
                }

                GLFW.GLFW_KEY_F20 -> {
                    "F20"
                }

                GLFW.GLFW_KEY_F21 -> {
                    "F21"
                }

                GLFW.GLFW_KEY_F22 -> {
                    "F22"
                }

                GLFW.GLFW_KEY_F23 -> {
                    "F23"
                }

                GLFW.GLFW_KEY_F24 -> {
                    "F24"
                }

                GLFW.GLFW_KEY_F25 -> {
                    "F25"
                }

                else -> {
                    val keyName = GLFW.glfwGetKeyName(key, 0) ?: return "None"
                    keyName.uppercase(Locale.getDefault())
                }
            }
        }

        fun getMouseName(button: Int): String {
            return if (button == GLFW.GLFW_KEY_UNKNOWN) {
                "None"
            } else when (button) {
                0 -> "Left Click"
                1 -> "Right Click"
                2 -> "Middle Click"
                else -> "MButton " + (button + 1)
            }

            // ima need some whiskey glasses...
            // cause ion wanna see the truth
            // shes prolly making out on the couch right now
            // with someone newwwwwwwwww

            // TODO: Add more mouse names
        }
    }

}