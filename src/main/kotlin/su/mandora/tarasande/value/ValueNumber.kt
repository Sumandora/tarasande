package su.mandora.tarasande.value

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import su.mandora.tarasande.base.value.Value

open class ValueNumber(owner: Any, name: String, val min: Double, var value: Double, val max: Double, val increment: Double) : Value(owner, name) {
    override fun save(): JsonElement {
        return JsonPrimitive(value)
    }

    override fun load(jsonElement: JsonElement) {
        value = jsonElement.asDouble
    }
}
