package com.paragon.backend.module

import com.paragon.Paragon
import com.paragon.backend.ToggleFeature
import com.paragon.backend.bind.Bind
import me.surge.animation.BoundedAnimation
import me.surge.animation.Easing

/**
 * @author surge
 * @since 11/02/2023
 */
open class Module(name: String, description: String, val category: Category) : ToggleFeature(name, description) {

    val animation = BoundedAnimation(0.0f, 100.0f, 200f, false, Easing.CUBIC_IN_OUT)

    var visible by bool("Visible", true, "The module's visibility in the ClickGUI")
    val key by bind("Key", Bind(this), "The key used to toggle the module")

    init {
        key.setInhibitor { _, bind -> setState(bind.state) }
        Paragon.keyboardManager.addBind(key)
    }

    override fun enable() {}
    override fun disable() {}

    override fun setState(state: Boolean) {
        this.isEnabled = state
        animation.state = state

        if (state) {
            enable()
            Paragon.bus.subscribe(this)
        } else {
            Paragon.bus.unsubscribe(this)
            disable()
        }

        Paragon.toastManager.info(this.name, "$name was toggled ${if (state) "on" else "off"}", 1000L)
    }

    open val info: () -> String? = { null }

    fun toggle() {
        key.state = !isEnabled
    }

    fun isActive(): Boolean = isEnabled || animation.linearFactor > 0.0
}