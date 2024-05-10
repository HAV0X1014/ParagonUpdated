package com.paragon.client.modules.visual

import com.paragon.backend.module.Category
import com.paragon.backend.module.Module

/**
 * @author aesthetical
 * @since 02/20/23
 */
object Toasts : Module("Toasts", "Configures toasts", Category.VISUAL) {

    init {
        visible = false
        setState(true)
    }

    val toggle by bool("Toggle", true, description = "If to show toasts for toggled modules")
    val totems by bool("Totems", true, description = "If to show toasts for totem pops")

}