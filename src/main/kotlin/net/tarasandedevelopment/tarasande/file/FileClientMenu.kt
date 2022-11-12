package net.tarasandedevelopment.tarasande.file

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.file.File
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.ElementMenuToggle

class FileClientMenu : File("ClientMenu") {

    override fun save(): JsonElement {
        val jsonObject = JsonObject()
        for (module in TarasandeMain.get().clientMenuSystem.list)
            if (module is ElementMenuToggle)
                jsonObject.addProperty(module.name, module.state)
        return jsonObject
    }

    override fun load(jsonElement: JsonElement) {
        val jsonObject = jsonElement as JsonObject
        for (module in TarasandeMain.get().clientMenuSystem.list)
            if (module is ElementMenuToggle)
                if (jsonObject.has(module.name))
                    module.state = jsonObject.get(module.name).asBoolean
    }
}