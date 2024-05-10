package com.paragon.backend.setting

import com.paragon.backend.bind.Bind
import net.minecraft.predicate.NumberRange
import net.minecraft.registry.Registry

/**
 * @author aesthetical
 * @since 02/20/23
 */
open class SettingContainer {
    val settingMap = mutableMapOf<String, Setting<*>>()

    protected fun bool(name: String, value: Boolean, description: String = "No description provided"): Setting<Boolean> {
        return setting(name, description, value)
    }

    // bad color spelling "colour" bruh
    protected fun colour(name: String, value: Colour, description: String = "No description provided"): Setting<Colour> {
        return setting(name, description, value)
    }

    protected fun bind(name: String, value: Bind, description: String = "No description provided"): Setting<Bind> {
        return setting(name, description, value)
    }

    protected inline fun <reified T : Enum<*>> enum(name: String, value: T, description: String = "No description provided"): Setting<T> {
        return setting(name, description, value)
    }

    protected fun float(
            name: String,
            value: Float,
            incrementation: Float,
            range: ClosedRange<Float>,
            description: String = "No description provided"
    ): Setting<Float> {

        val setting = Setting(name, description, value, range.start, range.endInclusive, incrementation)
        settingMap[name] = setting
        return setting
    }

    protected fun double(
            name: String,
            value: Double,
            incrementation: Double,
            range: ClosedRange<Double>,
            description: String = "No description provided"
    ): Setting<Double> {

        val setting = Setting(name, description, value, range.start, range.endInclusive, incrementation)
        settingMap[name] = setting
        return setting
    }

    protected fun int(
            name: String,
            value: Int,
            incrementation: Int,
            range: IntRange,
            description: String = "No description provided"
    ): Setting<Int> {

        val setting = Setting(name, description, value, range.first, range.last, incrementation)
        settingMap[name] = setting
        return setting
    }

    protected inline fun <reified T : Number> number(
            name: String,
            value: T,
            incrementation: T,
            range: NumberRange<T>,
            description: String = "No description provided"
    ): Setting<T> {

        val setting = Setting(name, description, value, range.min as T, range.max as T, incrementation)
        settingMap[name] = setting
        return setting
    }

    protected inline fun <reified T> registry(
        name: String,
        registry: Registry<T>,
        default: Boolean,
        description: String = "No description provided"
    ): RegistrySetting<*> {

        val setting = RegistrySetting(name, registry, description, default)
        settingMap[name] = setting
        return setting
    }

    protected inline fun <reified T> setting(name: String, description: String, value: T): Setting<T> {
        val setting = Setting(name, description, value)
        settingMap[name] = setting
        return setting
    }

    protected fun register(container: SettingContainer) {
        container.getSettings().forEach {
            settingMap[it.name] = it
        }
    }

    fun visibleWhen(condition: () -> Boolean) {
        this.getSettings().forEach {
            val original = it.visibility

            it.visibility = { original() && condition() }
        }
    }

    fun getSettings(): Collection<Setting<*>> = settingMap.values

    fun get(name: String): Setting<*>? {
        return settingMap[name]
    }

}