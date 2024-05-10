package com.paragon.backend.setting

import com.paragon.Paragon
import net.minecraft.registry.Registry
import org.json.JSONArray
import org.json.JSONObject
import java.util.function.Predicate
import kotlin.reflect.KProperty

/**
 * TODO: Categorise by given enum, e.g. EntityType -> SpawnGroup
 *
 * @author surge
 * @since 06/03/2023
 */
class RegistrySetting<T>(name: String, registry: Registry<T>, description: String, private val default: Boolean) : Setting<Registry<*>>(name, description, registry) {

    val states = hashMapOf<T, Boolean>().also {
        registry.sortedBy { it.toString() }.forEach { registryValue ->
            it[registryValue] = default
        }
    }

    fun getState(key: Any): Boolean {
        return getStateT(key as T)
    }

    fun getStateT(key: T): Boolean {
        return states[key] ?: default
    }

    fun setState(key: Any, value: Boolean) {
        setStateT(key as T, value)
    }

    fun setStateT(key: T, value: Boolean) {
        states[key] = value
    }

    fun enabled(): List<T> {
        return states.filter { it.value }.map { it.key }
    }

    fun disabled(): List<T> {
        return states.filter { !it.value }.map { it.key }
    }

    override fun write(json: JSONObject) {
        val obj = JSONObject()

        states.forEach { (key, value) ->
            obj.put(key.toString(), value)
        }

        json.put(this.name, obj)
    }

    override fun load(json: JSONObject) {
        try {
            val obj = json.getJSONObject(this.name)

            obj.keys().forEach { key ->
                val value = obj.getBoolean(key)
                val mappedKey = this.states.keys.first { it.toString() == key }

                if (mappedKey != null) {
                    this.states[mappedKey] = value
                }
            }
        } catch (exception: Exception) {
            Paragon.logger.warn("Failed to load $name")
        }
    }

}