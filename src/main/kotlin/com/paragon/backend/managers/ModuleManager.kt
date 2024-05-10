package com.paragon.backend.managers

import com.paragon.Paragon
import com.paragon.backend.bind.Bind
import com.paragon.backend.module.Module
import com.paragon.client.modules.combat.*
import com.paragon.client.modules.exploit.Disabler
import com.paragon.client.modules.exploit.FastProjectile
import com.paragon.client.modules.exploit.PingSpoof
import com.paragon.client.modules.exploit.ThunderLocator
import com.paragon.client.modules.movement.*
import com.paragon.client.modules.player.*
import com.paragon.client.modules.visual.*
import com.paragon.util.io.FileUtil
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.stream.Collectors

/**
 * @author surge
 * @since 11/02/2023
 */
class ModuleManager {

    val modules: List<Module>

    init {
        Paragon.bus.subscribe(this)

        modules = listOf(
            // Combat
            Aura,
            AutoTotem,
            Burrow,
            Criticals,
            Velocity,

            // Exploit
            Disabler,
            FastProjectile,
            PingSpoof,
            ThunderLocator,

            // Movement
            AntiVoid,
            AutoJump,
            ElytraFlight,
            Flight,
            InventoryMove,
            LongJump,
            NoFall,
            NoSlowDown,
            Speed,
            Sprint,
            Step,
            TridentBoost,

            // Player
            AirPlace,
            AutoElytra,
            AutoRespawn,
            AutoTool,
            BlockFly,
            FakePlayer,
            FastPlace,
            GameSpeed,
            LiquidPlace,
            NoTrace,
            PacketMine,
            Replenish,
            RotationLock,
            Stealer,

            // Visual
            ChinaHat,
            ClickGUI,
            ESP,
            FullBright,
            HoleESP,
            HUD,
            Tags,
            Tracers,
            Trajectories,
            UnfocusedCPU,
            ViewClip,
            Xray
        )

        if (FileUtil.PARAGON_PATH.resolve("configs").exists()) {
            FileUtil.PARAGON_PATH.resolve("configs").mkdir()
        }
    }

    fun load(name: String): String {
        val file = FileUtil.PARAGON_PATH.resolve("configs").resolve("$name.json")

        if (!file.exists()) {
            return "'$name.json' wasn't found in paragon/configs."
        }

        val json = JSONObject(file.readText(Charset.defaultCharset()))

        val modules = json.getJSONObject("modules") ?: return "'modules' group wasn't found!"

        Paragon.moduleManager.modules.forEach { module ->
            try {
                val data = modules.getJSONObject(module.name)

                try {
                    module.setState(data.getBoolean("enabled"))
                } catch (exception: Exception) {
                    Paragon.logger.warn("Failed to load state for ${module.name}")
                }

                module.settingMap.forEach { (name, setting) ->

                    if (setting.name == "Key" && setting.value is Bind) {
                        return@forEach
                    }

                    try {
                        setting.load(data)
                    } catch (exception: Exception) {
                        Paragon.logger.warn("Failed to load $name")
                    }
                }
            } catch (exception: Exception) {
                Paragon.logger.warn("Failed to load ${module.name}")
            }
        }

        return "Successfully loaded '$name.json'"
    }

    fun save(name: String) {
        val json = JSONObject()
        val modules = JSONObject()

        Paragon.moduleManager.modules.forEach { module ->
            val moduleJson = JSONObject()

            moduleJson.put("enabled", module.isEnabled)

            module.settingMap.forEach { (_, setting) ->
                if (setting.name == "Key" && setting.value is Bind) {
                    return@forEach
                }

                setting.write(moduleJson)
            }

            modules.put(module.name, moduleJson)
        }

        json.put("modules", modules)
        json.put("version", Paragon.version)

        val file = FileUtil.PARAGON_PATH.resolve("configs").resolve("$name.json")

        if (!file.exists()) {
            file.createNewFile()
        }

        FileUtil.write(file, json.toString(4))
    }

    fun getModules(predicate: (Module) -> Boolean): List<Module> {
        return modules.stream().filter(predicate).collect(Collectors.toList())
    }

}