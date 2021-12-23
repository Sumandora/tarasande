package su.mandora.tarasande.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import su.mandora.tarasande.base.value.Value

open class ValueBoolean(owner: Any, name: String, var value: Boolean) : Value(owner, name) {

	override fun save(): JsonElement {
		return JsonPrimitive(value)
	}

	override fun load(jsonElement: JsonElement) {
		value = jsonElement.asBoolean
	}
}