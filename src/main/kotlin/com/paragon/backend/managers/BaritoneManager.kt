package com.paragon.backend.managers

import baritone.api.BaritoneAPI
import baritone.api.Settings.Setting
import com.paragon.Paragon

/**
 * @author aesthetical
 * @since 02/23/23
 */
class BaritoneManager {

    private val saveStates = mutableMapOf<String, Any>()

    // TODO: Add ease of use methods here

    fun set(name: String, value: Any) {
        val settingsProvider = BaritoneAPI.getSettings()

        try {
            val field = settingsProvider.javaClass.getField(name)
            val settingObject = field.get(settingsProvider) as Setting<*>

            if (saveStates.containsKey(name) && saveStates[name] == value) {
                saveStates -= name
            } else {
                saveStates[name] = settingObject.value
            }

            // fuck kotlin this piece of shit language oh my god
            settingObject.javaClass.getField("value").set(settingObject, value)
        } catch (e: Exception) {
            Paragon.logger.warn("Tried to access baritone setting name $name while it does not exist")
        }
    }

    inline fun <reified T> get(name: String): T? {
        val settingsProvider = BaritoneAPI.getSettings()

        return try {
            val field = settingsProvider.javaClass.getField(name)
            val settingObject = field.get(settingsProvider) as Setting<*>

            settingObject.value as T
        } catch (e: Exception) {
            Paragon.logger.warn("Tried to access baritone setting name $name while it does not exist")

            null
        }
    }

    fun restoreValue(name: String) {
        if (saveStates.containsKey(name)) {
            set(name, saveStates[name]!!)
            saveStates -= name
        }
    }

    fun restoreValues() {
        if (saveStates.isNotEmpty()) {
            println("Reverting ${saveStates.size} baritone state${if (saveStates.size != 1) "s" else ""} to previous states")

            for ((k, v) in saveStates) {
                set(k, v)
            }

            saveStates.clear()
        }
    }

}