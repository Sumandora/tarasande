package net.tarasandedevelopment.tarasande.file

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.file.File

class FileModules : File("Modules") {

    override fun save(): JsonElement {
        val jsonObject = JsonObject()
        for (module in TarasandeMain.get().managerModule.list)
            jsonObject.addProperty(module.name, module._enabled)
        return jsonObject
    }

    override fun load(jsonElement: JsonElement) {
        val jsonObject = jsonElement as JsonObject
        for (module in TarasandeMain.get().managerModule.list)
            if (jsonObject.has(module.name))
                module.enabled = jsonObject.get(module.name).asBoolean
    }

}