package su.mandora.tarasande.file

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.file.File

class FileModules : File("Modules") {

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        for (module in TarasandeMain.get().managerModule?.list!!) if (module.enabled) jsonArray.add(module.name)
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray: JsonArray = jsonElement as JsonArray
        for (module in TarasandeMain.get().managerModule?.list!!) module.enabled = jsonArray.contains(JsonPrimitive(module.name))
    }

}