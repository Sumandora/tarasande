package su.mandora.tarasande.system.base.filesystem

import com.google.gson.JsonElement
import su.mandora.tarasande.Manager
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventShutdown
import su.mandora.tarasande.gson
import su.mandora.tarasande.logger
import java.io.FileWriter
import java.util.logging.Level

object ManagerFile : Manager<File>() {

    private val rootDirectory = java.io.File(System.getProperty("user.home") + java.io.File.separator + TARASANDE_NAME)

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
            val fileObj = java.io.File(rootDirectory, file.name)

            if (!fileObj.parentFile.exists())
                fileObj.parentFile.mkdirs()

            if (fileObj.exists() && backup)
                fileObj.renameTo(java.io.File(fileObj.path + "_backup"))

            val fileWriter = FileWriter(fileObj)
            fileWriter.write(gson.toJson(file.save()))
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
                logger.log(Level.CONFIG, file.name + " didn't load correctly!")
            }
        }
    }

    private fun internalLoad(file: File, backup: Boolean) {
        val fileObj = java.io.File(rootDirectory, file.name + (if (backup) "_backup" else ""))
        if (fileObj.exists()) {
            val content = fileObj.readBytes().decodeToString()
            val jsonElement = gson.fromJson(content, JsonElement::class.java)
            file.load(jsonElement)
        }
        file.loaded = true
    }
}

abstract class File(val name: String) {

    var loaded = false

    abstract fun save(): JsonElement
    abstract fun load(jsonElement: JsonElement)
}