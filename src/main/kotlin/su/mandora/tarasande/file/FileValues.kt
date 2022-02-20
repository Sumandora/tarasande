package su.mandora.tarasande.file

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import su.mandora.tarasande.TarasandeMain
import su.mandora.tarasande.base.file.File

class FileValues : File("Values") {

	override fun save(): JsonElement {
		val values = JsonObject()
		for (value in TarasandeMain.get().managerValue?.list!!) {
			var jsonObject: JsonObject
			if (values.has(value.owner.toString())) {
				jsonObject = values.getAsJsonObject(value.owner.toString())
			} else {
				jsonObject = JsonObject()
				values.add(value.owner.toString(), jsonObject)
			}
			jsonObject.add(value.name, value.save())
		}
		return values
	}

	override fun load(jsonElement: JsonElement) {
		val jsonObject = jsonElement as JsonObject
		for (value in TarasandeMain.get().managerValue?.list!!) {
			if (jsonObject.has(value.owner.toString())) {
				val jsonObject2 = jsonObject.getAsJsonObject(value.owner.toString())
				if (jsonObject2.has(value.name)) {
					try {
						value.load(jsonObject2[value.name])
						value.onChange()
					} catch (ignored: Throwable) {
					}
				}
			}
		}
	}
}