package com.paragon.util.io

import com.paragon.util.mc
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files

/**
 * @author aesthetical
 * @since 02/20/23
 */
object FileUtil {

    val PARAGON_PATH = mc.runDirectory.resolve("paragon")

    fun delete(file: File): Boolean {
        if (!file.exists()) {
            return false
        }

        return file.deleteRecursively()
    }

    fun write(file: File, content: String) {
        val outputStream = Files.newOutputStream(file.toPath())
        try {
            outputStream.write(content.toByteArray(), 0, content.length)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun read(file: File): String? {
        return read(Files.newInputStream(file.toPath()))
    }

    fun read(stream: InputStream?): String? {
        val builder = StringBuilder()
        try {
            var i: Int
            while (stream!!.read().also { i = it } != -1) {
                builder.append(i.toChar())
            }
        } catch (e: IOException) {
           return null
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e: IOException) {
                    return null
                }
            }
        }

        return builder.toString()
    }

}