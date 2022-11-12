package net.tarasandedevelopment.tarasande.value.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.tarasandedevelopment.tarasande.value.Value
import net.tarasandedevelopment.tarasande.value.impl.Valuecomponent.impl.ElementValueComponentNumberRange

open class ValueNumberRange(owner: Any, name: String, val min: Double, var minValue: Double, var maxValue: Double, val max: Double, val increment: Double, manage: Boolean = true) : Value(owner, name, ElementValueComponentNumberRange::class.java,  manage) {

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
