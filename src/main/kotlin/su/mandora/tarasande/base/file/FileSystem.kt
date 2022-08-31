package su.mandora.tarasande.base.file

import com.google.gson.JsonElement
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.Manager
import su.mandora.tarasande.file.FileAccounts
import su.mandora.tarasande.file.FileMenu
import su.mandora.tarasande.file.FileModules
import su.mandora.tarasande.file.FileValues
import java.io.FileWriter
import java.nio.file.Files

class ManagerFile : Manager<File>() {

    var loaded = false

    init {
        add(
            FileModules(),
            FileValues(),
            FileAccounts(),
            FileMenu()
        )
    }

    fun save() {
        if(!loaded) return

        for (file in list) {
            val fileObj = java.io.File(System.getProperty("user.home") + java.io.File.separator + TarasandeMain.get().name + java.io.File.separator + file.name)
            if (!fileObj.parentFile.exists()) fileObj.parentFile.mkdirs()
            val fileWriter = FileWriter(fileObj)
            fileWriter.write(file.encrypt(TarasandeMain.get().gson.toJson(file.save()))!!)
            fileWriter.close()
        }
    }

    fun load() {
        for (file in list) {
            val fileObj = java.io.File(System.getProperty("user.home") + java.io.File.separator + TarasandeMain.get().name + java.io.File.separator + file.name)
            if (fileObj.exists()) {
                val content = file.decrypt(String(Files.readAllBytes(fileObj.toPath())))
                if (content != null) {
                    val jsonElement = TarasandeMain.get().gson.fromJson(content, JsonElement::class.java)
                    if (jsonElement != null) file.load(jsonElement)
                    else System.err.println(file.name + " didn't load correctly!")
                }
            }
        }
        loaded = true
    }
}

abstract class File(val name: String) {

    abstract fun save(): JsonElement
    abstract fun load(jsonElement: JsonElement)

    open fun encrypt(input: String): String? {
        return input
    }

    open fun decrypt(input: String): String? {
        return input
    }
}