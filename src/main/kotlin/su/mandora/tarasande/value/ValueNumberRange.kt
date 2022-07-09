package su.mandora.tarasande.value

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import su.mandora.tarasande.base.value.Value

open class ValueNumberRange(owner: Any, name: String, val min: Double, var minValue: Double, var maxValue: Double, val max: Double, val increment: Double, manage: Boolean = true) : Value(owner, name, manage) {

    override fun save(): JsonElement {
        val jsonArray = JsonArray()
        jsonArray.add(minValue)
        jsonArray.add(maxValue)
        return jsonArray
    }

    override fun load(jsonElement: JsonElement) {
        val jsonArray: JsonArray = jsonElement.asJsonArray
        minValue = jsonArray.get(0).asDouble
        maxValue = jsonArray.get(1).asDouble
    }
}
