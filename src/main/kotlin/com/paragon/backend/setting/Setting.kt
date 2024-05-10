package com.paragon.backend.setting

import com.paragon.Paragon
import com.paragon.backend.Feature
import com.paragon.backend.event.events.paragon.SettingUpdateEvent
import org.json.JSONObject
import java.util.*
import kotlin.reflect.KProperty

/**
 * @author surge
 * @since 11/02/2023
 */
open class Setting<T>(name: String, description: String, value: T) : Feature(name, description) {

    var value: T = value
        private set

    // number setting bounds
    var minimum: T? = null
        private set

    var maximum: T? = null
        private set

    var incrementation: T? = null
        private set

    // enumeration ordinal
    private var index = 0

    // if the setting is visible inside the GUI
    var visibility: () -> Boolean = { true }

    // values that the current value cannot be
    val exclusions: List<T> = ArrayList()

    constructor(name: String, description: String, value: T, minimum: T, maximum: T, incrementation: T) : this(name, description, value) {
        this.minimum = minimum
        this.maximum = maximum
        this.incrementation = incrementation

        if (value is Enum<*>) {
            index = nextIndex
        }
    }

    fun setValue(value: T) {
        if (value != this.value) {
            Paragon.bus.post(SettingUpdateEvent(this))
        }

        if (value is Enum<*>) {
            index = nextIndex
        }

        this.value = value

        if (value is Enum<*> && exclusions.contains(value)) {
            setValue(nextEnum)
        }
    }

    open fun write(json: JSONObject) {
        json.put(this.name, this.value)
    }

    open fun load(json: JSONObject) {
        try {
            this.value = when (this.value) {
                is Int -> json.getInt(name) as T
                is Float -> json.getFloat(name) as T
                is Double -> json.getDouble(name) as T
                is Boolean -> json.getBoolean(name) as T
                is Enum<*> -> {
                    val enum = value as Enum<*>
                    val value = java.lang.Enum.valueOf(enum::class.java, json.getString(name))

                    run breakLoop@{
                        enum::class.java.enumConstants.forEachIndexed { index, enumValue ->
                            if (enumValue.name == value.name) {
                                this.index = index
                                return@breakLoop
                            }
                        }
                    }

                    value as T
                }

                else -> {
                    this.value
                }
            }
        } catch (exception: Exception) {
            Paragon.logger.warn("Failed to load $name")
        }
    }

    val nextEnum: T
        get() {
            val enum = value as Enum<*>

            return java.lang.Enum.valueOf(
                enum::class.java,
                enum.javaClass.enumConstants.map { it.name }[nextIndex]
            ) as T
        }

    val previousEnum: T
        get() {
            val enum = value as Enum<*>

            var prevIndex = index - 1
            if (prevIndex < 0) {
                prevIndex = enum.javaClass.enumConstants.size - 1
            }

            return java.lang.Enum.valueOf(
                    enum::class.java,
                    enum.javaClass.enumConstants.map { it.name }[prevIndex]
            ) as T
        }

    private val nextIndex: Int
        get() {
            val enum = value as Enum<*>

            return if (index + 1 > enum.javaClass.enumConstants.map { it.name }.size - 1) 0 else index + 1
        }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    operator fun setValue(thisRef: Any?, property: KProperty<*>, v: T) {
        value = v
    }

    infix fun visibility(condition: () -> Boolean): Setting<T> {
        this.visibility = condition

        return this
    }

}