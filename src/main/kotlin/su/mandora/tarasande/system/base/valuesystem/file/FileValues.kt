package su.mandora.tarasande.system.base.valuesystem.file

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import su.mandora.tarasande.system.base.filesystem.File
import su.mandora.tarasande.system.base.valuesystem.ManagerValue

class FileValues : File("Values") {

    override fun save(): JsonElement {
        val values = JsonObject()
        for (value in ManagerValue.list) {
            var jsonObject: JsonObject
            if (values.has(value.owner.javaClass.name)) {
                jsonObject = values.getAsJsonObject(value.owner.javaClass.name)
            } else {
                jsonObject = JsonObject()
                values.add(value.owner.javaClass.name, jsonObject)
            }
            try {
                val jsonElement = value.save()
                if (jsonElement != null)
                    jsonObject.add(value.name, jsonElement)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        return values
    }

    override fun load(jsonElement: JsonElement) {
        val jsonObject = jsonElement as JsonObject
        for (value in ManagerValue.list) {
            if (jsonObject.has(value.owner.javaClass.name)) {
                val jsonObject2 = jsonObject.getAsJsonObject(value.owner.javaClass.name)
                if (jsonObject2.has(value.name)) {
                    try {
                        value.load(jsonObject2[value.name])
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}