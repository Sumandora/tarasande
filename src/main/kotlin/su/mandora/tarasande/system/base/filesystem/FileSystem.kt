package su.mandora.tarasande.system.base.filesystem

import com.google.gson.JsonElement
import su.mandora.tarasande.Manager
import su.mandora.tarasande.TARASANDE_NAME
import su.mandora.tarasande.event.EventDispatcher
import su.mandora.tarasande.event.impl.EventShutdown
import su.mandora.tarasande.gson

// I don't like this name collision, but I can't really come up with better names for this... TODO

typealias JavaFile = java.io.File

object ManagerFile : Manager<File>() {

    @Suppress("MemberVisibilityCanBePrivate") // Packages could want that
    val rootDirectory = JavaFile(System.getProperty("user.home") + JavaFile.separator + TARASANDE_NAME)

    init {
        EventDispatcher.add(EventShutdown::class.java) {
            saveAll()
        }
    }

    override fun insert(obj: File, index: Int) {
        super.insert(obj, index)
        try {
            obj.loadFile()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun saveAll() {
        for (file in list) {
            file.saveFile()
        }
    }
}

abstract class File(val name: String) {

    abstract fun save(): JsonElement
    abstract fun load(jsonElement: JsonElement)

    fun getPath() = JavaFile(ManagerFile.rootDirectory, name)

    fun saveFile() {
        val fileObj = getPath()

        if (!fileObj.parentFile.exists())
            fileObj.parentFile.mkdirs()

        fileObj.writeText(gson.toJson(save()))
    }

    fun loadFile() {
        val fileObj = getPath()

        if (!fileObj.exists())
            return

        val content = fileObj.readBytes().decodeToString()
        val jsonElement = gson.fromJson(content, JsonElement::class.java)
        load(jsonElement)
    }
}