package com.paragon.client.ui.configuration.aesthetical.elements

import com.paragon.util.rendering.ui.Element

abstract class SettingElement : Element() {
    open val visible: () -> Boolean = { true }

    open fun getTotalHeight(): Float {
        return height
    }
}