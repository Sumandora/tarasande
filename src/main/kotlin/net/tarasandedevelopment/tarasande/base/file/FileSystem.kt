package net.tarasandedevelopment.tarasande.base.file

import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.Manager
import net.tarasandedevelopment.tarasande.file.FileAccounts
import net.tarasandedevelopment.tarasande.file.FileMenu
import net.tarasandedevelopment.tarasande.file.FileModules
import net.tarasandedevelopment.tarasande.file.values.FileValuesBinds
import net.tarasandedevelopment.tarasande.file.values.FileValuesNonBinds
import java.io.FileWriter
import java.nio.file.Files

class ManagerFile : Manager<File>() {

    init {
        add(
            FileModules(),
            FileValuesBinds(),
            FileValuesNonBinds(),
            FileAccounts(),
            FileMenu()
        )
    }

    fun save(backup: Boolean) {
        for (file in list) {
            val fileObj = java.io.File(System.getProperty("user.home") + java.io.File.separator + TarasandeMain.get().name + java.io.File.separator + file.name)

            if (!fileObj.parentFile.exists())
                fileObj.parentFile.mkdirs()

            if (fileObj.exists() && backup)
                fileObj.renameTo(java.io.File(fileObj.path + "_backup"))

            val fileWriter = FileWriter(fileObj)
            fileWriter.write(file.encrypt(TarasandeMain.get().gson.toJson(file.save()))!!)
            fileWriter.close()
        }
    }

    fun load() {
        for (file in list) {
            try {
                internalLoad(file, false)
            } catch (t: Throwable) {
                try {
                    internalLoad(file, true)
                } catch (t: Throwable) {
                    TarasandeMain.get().logger.error(file.name + "didn't load correctly!")
                }
            }
        }
    }

    private fun internalLoad(file: File, backup: Boolean) {
        val fileObj = java.io.File(System.getProperty("user.home") + java.io.File.separator + TarasandeMain.get().name + java.io.File.separator + file.name + if (backup) "_backup" else "")
        val content = file.decrypt(String(Files.readAllBytes(fileObj.toPath()))) ?: error(file.name + "'s content is empty")
        val jsonElement = TarasandeMain.get().gson.fromJson(content, JsonElement::class.java)
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