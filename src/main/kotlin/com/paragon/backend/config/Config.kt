package com.paragon.backend.config

import com.paragon.Paragon
import com.paragon.util.io.FileUtil

/**
 * @author aesthetical
 * @since 02/20/23
 */
abstract class Config(path: String, create: Boolean = true) {

    val file = FileUtil.PARAGON_PATH.resolve(path)

    init {
        if (create && !file.exists()) {
            if (path.endsWith("/")) {
                file.mkdirs()
            } else {
                file.createNewFile()
            }
        }

        Paragon.configManager.add(this)
    }

    abstract fun save()
    abstract fun load()

}