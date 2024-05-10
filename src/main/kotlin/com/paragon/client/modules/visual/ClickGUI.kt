package com.paragon.client.modules.visual

import com.paragon.backend.module.Category
import com.paragon.backend.module.Module
import com.paragon.client.ui.configuration.aesthetical.AestheticalUI
import com.paragon.client.ui.configuration.surge.SurgeUI
import com.paragon.util.mc
import com.paragon.util.nullCheck
import org.lwjgl.glfw.GLFW

/**
 * @author surge
 * @since 11/02/2023
 */
object ClickGUI : Module("Click GUI", "The main GUI of the client", Category.VISUAL) {

    private val mode by enum("Mode", Mode.SURGE, "The type of click gui")

    var surgeUi: SurgeUI? = null
    var aestheticalUI: AestheticalUI? = null

    init {
        key.code = GLFW.GLFW_KEY_RIGHT_SHIFT
        visible = false
    }

    override fun enable() {
        if (nullCheck()) {
            return
        }

        mc.setScreen(
                when (mode) {
                    Mode.SURGE -> surgeUi
                    Mode.AESTHETICAL -> aestheticalUI
                    else -> null
                }
        )

        toggle()
    }

    enum class Mode {
        SURGE, AESTHETICAL
    }


}