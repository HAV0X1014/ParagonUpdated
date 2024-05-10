package com.paragon.backend

/**
 * @author aesthetical
 * @since 02/17/23
 */
open class ToggleFeature(name: String, description: String) : Feature(name, description) {

    var isEnabled = false
        protected set

    open fun enable() {}
    open fun disable() {}

    open fun setState(state: Boolean) {
        isEnabled = state

        if (state) {
            enable()
        } else {
            disable()
        }
    }

}