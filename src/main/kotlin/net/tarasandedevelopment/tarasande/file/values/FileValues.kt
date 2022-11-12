package net.tarasandedevelopment.tarasande.file.values

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.tarasandedevelopment.tarasande.TarasandeMain
import net.tarasandedevelopment.tarasande.base.file.File
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.Value
import java.util.function.Function

open class FileValues(name: String, private val condition: Function<Value, Boolean>) : File(name) {

    override fun save(): JsonElement {
        val values = JsonObject()
        for (value in TarasandeMain.managerValue.list) {
            if (!condition.apply(value))
                continue

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
        for (value in TarasandeMain.managerValue.list) {
            if (!condition.apply(value))
                continue

            if (jsonObject.has(value.owner.javaClass.name)) {
                val jsonObject2 = jsonObject.getAsJsonObject(value.owner.javaClass.name)
                if (jsonObject2.has(value.name)) {
                    try {
                        value.load(jsonObject2[value.name])
                        value.onChange()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}