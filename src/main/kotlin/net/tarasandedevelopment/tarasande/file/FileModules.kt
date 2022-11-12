package net.tarasandedevelopment.tarasande.file

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.file.File

class FileModules : File("Modules") {

    override fun save(): JsonElement {
        val jsonObject = JsonObject()
        for (module in TarasandeMain.get().moduleSystem.list)
            jsonObject.addProperty(module.name, module.enabled)
        return jsonObject
    }

    override fun load(jsonElement: JsonElement) {
        val jsonObject = jsonElement as JsonObject
        for (module in TarasandeMain.get().moduleSystem.list)
            if (jsonObject.has(module.name))
                module.enabled = jsonObject.get(module.name).asBoolean
    }

}