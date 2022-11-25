package net.tarasandedevelopment.tarasande.system.feature.modulesystem.file

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.tarasandedevelopment.tarasande.system.base.filesystem.File
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule

class FileModules(private val moduleSystem: ManagerModule) : File("Modules") {

    override fun save(): JsonElement {
        val jsonObject = JsonObject()
        for (module in moduleSystem.list)
            jsonObject.addProperty(module.name, module.enabled)
        return jsonObject
    }

    override fun load(jsonElement: JsonElement) {
        val jsonObject = jsonElement as JsonObject
        for (module in moduleSystem.list)
            if (jsonObject.has(module.name))
                module.enabled = jsonObject.get(module.name).asBoolean
    }

}