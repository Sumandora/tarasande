package net.tarasandedevelopment.tarasande.system.base.filesystem

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.Manager
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.event.EventShutdown
import su.mandora.event.EventDispatcher
import java.io.FileWriter
import java.nio.file.Files
import java.util.logging.Level

object ManagerFile : Manager<File>() {

    init {
        EventDispatcher.add(EventShutdown::class.java) {
            save(true)
        }
    }

    override fun insert(obj: File, index: Int) {
        super.insert(obj, index)
        load(obj)
    }

    fun save(backup: Boolean) {
        for (file in list) {
            val fileObj = java.io.File(TarasandeMain.rootDirectory, file.name)

            if (!fileObj.parentFile.exists())
                fileObj.parentFile.mkdirs()

            if (fileObj.exists() && backup)
                fileObj.renameTo(java.io.File(fileObj.path + "_backup"))

            val fileWriter = FileWriter(fileObj)
            fileWriter.write(file.encrypt(TarasandeMain.gson.toJson(file.save()))!!)
            fileWriter.close()
        }
    }

    fun load(file: File) {
        try {
            internalLoad(file, false)
        } catch (t: Throwable) {
            t.printStackTrace()
            try {
                internalLoad(file, true)
            } catch (t: Throwable) {
                t.printStackTrace()
                TarasandeMain.logger.log(Level.CONFIG, file.name + " didn't load correctly!")
            }
        }
    }

    private fun internalLoad(file: File, backup: Boolean) {
        val fileObj = java.io.File(TarasandeMain.rootDirectory, file.name + (if (backup) "_backup" else ""))
        val content = file.decrypt(String(Files.readAllBytes(fileObj.toPath()))) ?: error(file.name + "'s content is invalid")
        val jsonElement = TarasandeMain.gson.fromJson(content, JsonElement::class.java)
        file.load(jsonElement)
        file.loaded = true
    }
}

abstract class File(val name: String) {

    var loaded = false

    abstract fun save(): JsonElement
    abstract fun load(jsonElement: JsonElement)

    open fun encrypt(input: String): String? {
        return input
    }

    open fun decrypt(input: String): String? {
        return input
    }
}