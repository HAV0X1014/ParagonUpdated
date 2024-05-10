package com.paragon.backend.managers

import com.paragon.Paragon
import com.paragon.backend.bind.Bind
import com.paragon.backend.bind.DeviceType
import com.paragon.backend.config.Config
import com.paragon.backend.event.events.input.io.KeyEvent
import com.paragon.backend.event.events.input.io.MouseEvent
import com.paragon.backend.module.Module
import com.paragon.util.io.FileUtil
import com.paragon.util.mc
import me.bush.eventbus.annotation.EventListener
import org.json.JSONObject
import org.lwjgl.glfw.GLFW
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author aesthetical
 * @since 02/20/2023
 */
class KeyboardManager {

    private val bindList = CopyOnWriteArrayList<Bind>()

    init {
        Paragon.bus.subscribe(this)

        object : Config("binds.json") {

            override fun save() {
                val jsonObject = JSONObject()

                for (bind in bindList) {
                    val obj = JSONObject()

                    if (bind.feature is Module) {
                        obj.put("featureType", "module")
                    }

                    obj.put("code", bind.code)
                    obj.put("deviceType", bind.type.name)
                    obj.put("persistent", bind.isPersistent)

                    jsonObject.put(bind.feature.name, obj)
                }

                FileUtil.write(file, jsonObject.toString(4))
            }

            override fun load() {
                val content = FileUtil.read(file)

                if (content.isNullOrEmpty()) {
                    return
                }

                val jsonObject = JSONObject(content)

                for ((k, v) in jsonObject.toMap()) {
                    if (v !is HashMap<*, *>) {
                        continue
                    }

                    if (!v.containsKey("featureType")) {
                        continue
                    }

                    when (v["featureType"] as String) {
                        "module" -> {
                            val module = Paragon.moduleManager.modules.find { it.name.equals(k, ignoreCase = true) }
                            if (module == null) {
                                Paragon.logger.warn("Unknown module with name $k")
                                continue
                            }

                            if (v.containsKey("code")) {
                                module.key.code = v["code"] as Int
                            }

                            if (v.containsKey("deviceType")) {
                                module.key.type = DeviceType.valueOf((v["deviceType"] as String).uppercase())
                            }

                            if (v.containsKey("persistent")) {
                                module.key.isPersistent = v["persistent"] as Boolean
                            }
                        }

                        "macro" -> Paragon.logger.warn("Modified binds file! Macros not implemented. Try later... ($k)")
                        else -> Paragon.logger.warn("Unknown feature type ${v["featureType"] as String}")
                    }
                }
            }
        }
    }

    @EventListener
    fun onKey(event: KeyEvent) {
        if (event.code == GLFW.GLFW_KEY_UNKNOWN || mc.currentScreen != null || event.action > 1) {
            return
        }

        bindList.forEach { bind ->
            if (bind.code == event.code && bind.type == DeviceType.KEYBOARD) {
                if (!bind.isPersistent) {
                    bind.state = event.action == GLFW.GLFW_PRESS
                } else {
                    if (event.action == GLFW.GLFW_RELEASE) {
                        bind.state = !bind.state
                    }
                }
            }
        }
    }

    @EventListener
    fun onMouse(event: MouseEvent) {
        if (event.button == GLFW.GLFW_KEY_UNKNOWN || mc.currentScreen != null || event.action > 1) {
            return
        }

        bindList.forEach { bind ->
            if (bind.code == event.button && bind.type == DeviceType.MOUSE) {
                if (!bind.isPersistent) {
                    bind.state = event.action == GLFW.GLFW_PRESS
                } else {
                    if (event.action == GLFW.GLFW_RELEASE) {
                        bind.state = !bind.state
                    }
                }
            }
        }
    }

    fun addBind(bind: Bind) {
        bindList.add(bind)

        if (bind.type == DeviceType.UNKNOWN) {
            Paragon.logger.warn("Bind with code {} signed to unknown device with feature {} linked", bind.code, bind.feature)
        }
    }

}