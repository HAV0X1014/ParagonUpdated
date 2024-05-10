package com.paragon.backend.managers

import com.paragon.Paragon
import com.paragon.backend.config.Config
import com.paragon.util.calculations.Timer

/**
 * @author aesthetical
 * @since 02/20/23
 */
class ConfigManager {

    private val configList = mutableListOf<Config>()

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            println("Saving ${configList.size} config${if (configList.size != 1) "s" else ""}...")
            val timer = Timer()
            timer.reset()

            for (config in configList) {
                try {
                    config.save()
                } catch (e: Exception) {
                    Paragon.logger.error("Exception while saving ${config}, stacktrace is below")
                    e.printStackTrace()
                }
            }

            val ms = timer.timeMs()
            println("Saved data in ${ms}ms.")

            println("Reverting baritone states...")
            Paragon.baritoneManager.restoreValues()
        })
    }

    fun add(config: Config) {
        configList.add(config)
    }

    fun loadAll() {
        configList.forEach { it.load() }
    }

}