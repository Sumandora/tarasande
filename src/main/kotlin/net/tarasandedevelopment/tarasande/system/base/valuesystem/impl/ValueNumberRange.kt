package net.tarasandedevelopment.tarasande.system.base.valuesystem.impl

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import io.netty.util.internal.ThreadLocalRandom
import net.tarasandedevelopment.tarasande.system.base.valuesystem.Value
import net.tarasandedevelopment.tarasande.system.base.valuesystem.valuecomponent.impl.ElementWidthValueComponentNumberRange

open class ValueNumberRange(owner: Any, name: String, val min: Double, var minValue: Double, var maxValue: Double, val max: Double, val increment: Double, val exceed: Boolean = true, manage: Boolean = true) : Value(owner, name, ElementWidthValueComponentNumberRange::class.java, manage) {

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

    fun randomNumber(): Double {
        return if(minValue == maxValue) minValue else ThreadLocalRandom.current().nextDouble(minValue, maxValue)
    }

    fun interpolate(t: Double): Double {
        return minValue + (maxValue - minValue) * t
    }
}
